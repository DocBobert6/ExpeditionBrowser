package com.expedition.browser.engine

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import java.io.ByteArrayInputStream

class ExpeditionWebViewClient(
    private val parameterStripper: ParameterStripper = ParameterStripper(),
    private val onPageStarted: (String) -> Unit = {},
    private val onPageFinished: (String) -> Unit = {},
    private val onProgress: (Int) -> Unit = {}
) : WebViewClientCompat() {

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val originalUrl = request.url.toString()

        // Never interfere with OAuth / auth flows
        if (parameterStripper.isAuthDomain(originalUrl)) {
            return false
        }

        // Strip tracking parameters on top-level navigations
        val sanitized = parameterStripper.sanitizeUrl(originalUrl)
        if (sanitized != originalUrl) {
            view.loadUrl(sanitized)
            return true
        }
        return false
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()
        if (TrackerBlocklist.isBlocked(url)) {
            // Return empty response – blocked by Expedition Privacy
            return WebResourceResponse(
                "text/plain",
                "utf-8",
                403,
                "Blocked by Expedition Privacy",
                mapOf("Access-Control-Allow-Origin" to "*"),
                ByteArrayInputStream(ByteArray(0))
            )
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        url?.let { onPageStarted(it) }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        url?.let { onPageFinished(it) }
    }
}
