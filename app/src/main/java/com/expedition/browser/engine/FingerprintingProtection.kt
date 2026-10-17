package com.expedition.browser.engine

import android.webkit.WebView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

enum class FingerprintProfile {
    STRICT,    // full noise
    BALANCED,  // reduced canvas/audio noise for maps/charts
    OFF
}

/**
 * Injects document-start JS for fingerprinting defenses.
 * Profile controls aggressiveness so complex web apps can still function.
 */
object FingerprintingProtection {

    fun apply(webView: WebView, profile: FingerprintProfile) {
        if (profile == FingerprintProfile.OFF) return
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) return

        val script = when (profile) {
            FingerprintProfile.STRICT -> strictScript
            FingerprintProfile.BALANCED -> balancedScript
            FingerprintProfile.OFF -> return
        }

        WebViewCompat.addDocumentStartJavaScript(webView, script, setOf("*"))
    }

    private val strictScript = """
        (function() {
            // Canvas noise
            const origGetImageData = CanvasRenderingContext2D.prototype.getImageData;
            CanvasRenderingContext2D.prototype.getImageData = function() {
                const imageData = origGetImageData.apply(this, arguments);
                for (let i = 0; i < imageData.data.length; i += 4) {
                    imageData.data[i]     ^= (Math.random() * 2) | 0;
                    imageData.data[i + 1] ^= (Math.random() * 2) | 0;
                }
                return imageData;
            };

            // Audio noise
            if (window.AudioBuffer) {
                const origGetChannelData = AudioBuffer.prototype.getChannelData;
                AudioBuffer.prototype.getChannelData = function() {
                    const data = origGetChannelData.apply(this, arguments);
                    for (let i = 0; i < data.length; i += 100) {
                        data[i] += (Math.random() - 0.5) * 0.0001;
                    }
                    return data;
                };
            }

            // WebGL
            const getParameter = WebGLRenderingContext.prototype.getParameter;
            WebGLRenderingContext.prototype.getParameter = function(param) {
                if (param === 37445) return 'Intel Inc.'; // UNMASKED_VENDOR
                if (param === 37446) return 'Intel Iris OpenGL Engine'; // UNMASKED_RENDERER
                return getParameter.apply(this, arguments);
            };

            // Hardware concurrency / device memory
            Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 4 });
            Object.defineProperty(navigator, 'deviceMemory', { get: () => 4 });

            // Screen size rounding
            Object.defineProperty(screen, 'width', { get: () => Math.round(screen.width / 8) * 8 });
            Object.defineProperty(screen, 'height', { get: () => Math.round(screen.height / 8) * 8 });

            // Plugins
            Object.defineProperty(navigator, 'plugins', { get: () => [] });

            // Languages limited
            Object.defineProperty(navigator, 'languages', { get: () => ['en-US', 'en'] });

            // Battery
            if (navigator.getBattery) {
                navigator.getBattery = undefined;
            }
        })();
    """.trimIndent()

    private val balancedScript = """
        (function() {
            // Lighter canvas noise – only on small readbacks to avoid breaking maps/charts
            const origGetImageData = CanvasRenderingContext2D.prototype.getImageData;
            CanvasRenderingContext2D.prototype.getImageData = function(sx, sy, sw, sh) {
                const imageData = origGetImageData.apply(this, arguments);
                if (sw * sh < 10000) { // only small canvases
                    for (let i = 0; i < imageData.data.length; i += 16) {
                        imageData.data[i] ^= 1;
                    }
                }
                return imageData;
            };

            // WebGL vendor/renderer only
            const getParameter = WebGLRenderingContext.prototype.getParameter;
            WebGLRenderingContext.prototype.getParameter = function(param) {
                if (param === 37445) return 'Intel Inc.';
                if (param === 37446) return 'Intel Iris OpenGL Engine';
                return getParameter.apply(this, arguments);
            };

            Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 4 });
            Object.defineProperty(navigator, 'deviceMemory', { get: () => 4 });
            Object.defineProperty(navigator, 'plugins', { get: () => [] });
        })();
    """.trimIndent()
}
