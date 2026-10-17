package com.expedition.browser.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.webkit.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Sliding-window tab hibernation.
 * Keeps ~4 live WebViews. Background tabs are detached and replaced by a high-res bitmap thumbnail.
 * On restore: re-inflate WebView + loadUrl + show thumbnail until first layout.
 */
class TabHibernationManager(private val context: Context) {

    companion object {
        const val MAX_LIVE_TABS = 4
    }

    data class HibernatedTab(
        val tabId: String,
        val url: String,
        val title: String,
        val thumbnail: Bitmap?,
        val lastAccess: Long = System.currentTimeMillis()
    )

    private val liveWebViews = ConcurrentHashMap<String, WebView>()
    private val hibernated = ConcurrentHashMap<String, HibernatedTab>()

    fun registerLive(tabId: String, webView: WebView) {
        liveWebViews[tabId] = webView
        hibernated.remove(tabId)
        enforceWindow()
    }

    fun unregister(tabId: String) {
        liveWebViews.remove(tabId)?.destroy()
        hibernated.remove(tabId)
    }

    suspend fun hibernateIfNeeded(tabId: String, webView: WebView, url: String, title: String) {
        if (liveWebViews.size <= MAX_LIVE_TABS) return
        // Find least-recently-used live tab that is not the current one
        // (simple implementation: hibernate the requested one if over limit)
        val thumb = captureThumbnail(webView)
        withContext(Dispatchers.Main) {
            // Detach from parent if any
            (webView.parent as? android.view.ViewGroup)?.removeView(webView)
            webView.destroy()
            liveWebViews.remove(tabId)
        }
        hibernated[tabId] = HibernatedTab(tabId, url, title, thumb)
    }

    fun getHibernated(tabId: String): HibernatedTab? = hibernated[tabId]

    fun isLive(tabId: String): Boolean = liveWebViews.containsKey(tabId)

    fun getLive(tabId: String): WebView? = liveWebViews[tabId]

    private fun enforceWindow() {
        if (liveWebViews.size <= MAX_LIVE_TABS) return
        // Simple LRU-ish: remove oldest entry beyond limit (real impl would track timestamps)
        val excess = liveWebViews.size - MAX_LIVE_TABS
        liveWebViews.keys.take(excess).forEach { id ->
            liveWebViews.remove(id)?.let { wv ->
                // Caller should have already captured thumbnail if needed
                wv.destroy()
            }
        }
    }

    private suspend fun captureThumbnail(view: View): Bitmap? = withContext(Dispatchers.Default) {
        try {
            if (view.width <= 0 || view.height <= 0) return@withContext null
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            // Downscale for memory
            val maxDim = 720
            val scale = minOf(1f, maxDim.toFloat() / maxOf(bitmap.width, bitmap.height))
            if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt(),
                    (bitmap.height * scale).toInt(),
                    true
                ).also { bitmap.recycle() }
            } else bitmap
        } catch (_: Exception) {
            null
        }
    }

    fun clearAll() {
        liveWebViews.values.forEach { it.destroy() }
        liveWebViews.clear()
        hibernated.values.forEach { it.thumbnail?.recycle() }
        hibernated.clear()
    }
}
