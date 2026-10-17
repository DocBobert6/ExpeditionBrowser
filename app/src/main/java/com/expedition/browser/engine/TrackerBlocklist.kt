package com.expedition.browser.engine

import android.net.Uri

/**
 * Lightweight bundled blocklist for common trackers / ad domains.
 * In production this would be expanded or loaded from assets.
 */
object TrackerBlocklist {

    private val blockedHosts = setOf(
        "googlesyndication.com",
        "google-analytics.com",
        "googleadservices.com",
        "doubleclick.net",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "connect.facebook.net",
        "facebook.com/tr",
        "static.ads-twitter.com",
        "ads-api.twitter.com",
        "analytics.twitter.com",
        "scorecardresearch.com",
        "quantserve.com",
        "outbrain.com",
        "taboola.com",
        "adsafeprotected.com",
        "moatads.com",
        "amazon-adsystem.com",
        "adsystem.amazon.com"
    )

    fun isBlocked(url: String): Boolean {
        return try {
            val host = Uri.parse(url).host?.lowercase() ?: return false
            blockedHosts.any { blocked ->
                host == blocked || host.endsWith(".$blocked")
            }
        } catch (_: Exception) {
            false
        }
    }
}
