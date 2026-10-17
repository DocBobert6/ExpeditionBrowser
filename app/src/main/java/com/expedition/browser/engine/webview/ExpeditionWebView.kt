package com.expedition.browser.engine.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.expedition.browser.engine.ExpeditionWebViewClient
import com.expedition.browser.engine.FingerprintingProtection
import com.expedition.browser.engine.FingerprintProfile
import com.expedition.browser.engine.ParameterStripper

/**
 * Production WebView wrapper used by Expedition Browser.
 * Handles privacy injection, dark mode preference, and exposes simple navigation helpers.
 */
@SuppressLint("SetJavaScriptEnabled")
class ExpeditionWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs) {

    var onPageStartedCallback: ((String) -> Unit)? = null
    var onPageFinishedCallback: ((String, String?) -> Unit)? = null
    var onProgressCallback: ((Int) -> Unit)? = null
    var onTitleCallback: ((String?) -> Unit)? = null
    var onScrollChangedCallback: ((Int, Int) -> Unit)? = null

    private val parameterStripper = ParameterStripper()

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        configureSettings()
        webViewClient = ExpeditionWebViewClient(
            parameterStripper = parameterStripper,
            onPageStarted = { url -> onPageStartedCallback?.invoke(url) },
            onPageFinished = { url -> onPageFinishedCallback?.invoke(url, title) },
            onProgress = { /* chrome client handles progress */ }
        )
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                onProgressCallback?.invoke(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                onTitleCallback?.invoke(title)
            }
        }
    }

    private fun configureSettings() {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = userAgentString // leave default as required
        }

        // Force dark / algorithmic darkening when available
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true)
        }
    }

    fun applyFingerprintProfile(profile: FingerprintProfile) {
        FingerprintingProtection.apply(this, profile)
    }

    fun navigate(url: String) {
        if (url.isBlank() || url == "about:blank") return
        loadUrl(url)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangedCallback?.invoke(t, t - oldt)
    }

    fun captureThumbnail(): Bitmap? {
        return try {
            if (width <= 0 || height <= 0) return null
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            draw(canvas)
            // Downscale
            val max = 720
            val scale = minOf(1f, max.toFloat() / maxOf(width, height))
            if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    bitmap,
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                    true
                ).also { bitmap.recycle() }
            } else bitmap
        } catch (_: Exception) {
            null
        }
    }

    fun setTextZoom(percent: Int) {
        settings.textZoom = percent
    }

    fun runFind(query: String) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            findAllAsync(query)
        }
    }

    fun runFindNext(forward: Boolean) {
        findNext(forward)
    }

    fun runClearMatches() {
        clearMatches()
    }

}
