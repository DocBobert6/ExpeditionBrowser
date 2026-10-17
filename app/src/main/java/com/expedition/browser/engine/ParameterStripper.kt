package com.expedition.browser.engine

import android.net.Uri

/**
 * Strips tracking parameters while protecting OAuth / login flows.
 * Used from shouldOverrideUrlLoading for top-level navigations.
 */
class ParameterStripper {

    private val trackingParams = listOf(
        "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content",
        "utm_id", "utm_source_platform", "utm_creative_format", "utm_marketing_tactic",
        "fbclid", "gclid", "msclkid", "mc_eid", "igshid", "twclid", "li_fat_id",
        "_ga", "_gl", "mc_cid", "mc_eid", "oly_anon_id", "oly_enc_id"
    )

    // Domains / path patterns where we must never strip parameters
    private val authDomainPatterns = listOf(
        "accounts.google.com",
        "login.microsoftonline.com",
        "login.live.com",
        "appleid.apple.com",
        "github.com/login",
        "github.com/session",
        "auth0.com",
        "okta.com",
        "oauth",
        "sso.",
        "signin.",
        "login.",
        "accounts.",
        "id.",
        "auth."
    )

    fun isAuthDomain(url: String): Boolean {
        return try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase() ?: return false
            val path = uri.path?.lowercase() ?: ""
            authDomainPatterns.any { pattern ->
                host.contains(pattern) || path.contains(pattern)
            }
        } catch (_: Exception) {
            false
        }
    }

    fun sanitizeUrl(url: String): String {
        if (isAuthDomain(url)) return url
        return try {
            val uri = Uri.parse(url)
            val names = uri.queryParameterNames
            if (names.isEmpty()) return url

            val hasTracking = names.any { name ->
                trackingParams.any { tp -> name.equals(tp, ignoreCase = true) || name.startsWith("utm_", ignoreCase = true) }
            }
            if (!hasTracking) return url

            val builder = uri.buildUpon().clearQuery()
            for (name in names) {
                val isTracking = trackingParams.any { tp ->
                    name.equals(tp, ignoreCase = true) || name.startsWith("utm_", ignoreCase = true)
                }
                if (!isTracking) {
                    uri.getQueryParameters(name).forEach { value ->
                        builder.appendQueryParameter(name, value)
                    }
                }
            }
            builder.build().toString()
        } catch (_: Exception) {
            url
        }
    }
}
