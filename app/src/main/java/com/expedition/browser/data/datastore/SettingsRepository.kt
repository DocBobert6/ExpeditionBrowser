package com.expedition.browser.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.expedition.browser.engine.FingerprintProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "expedition_settings")

enum class ToolbarLayout { COMPACT, BOTTOM, TOP }

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LAYOUT = stringPreferencesKey("toolbar_layout")
        val GLASS_TINT = floatPreferencesKey("glass_tint")
        val REDUCE_TRANSPARENCY = booleanPreferencesKey("reduce_transparency")
        val FINGERPRINT_PROFILE = stringPreferencesKey("fingerprint_profile")
        val DARK_THEME = stringPreferencesKey("dark_theme") // "system", "light", "dark"
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
    }

    val toolbarLayout: Flow<ToolbarLayout> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.LAYOUT]) {
            "BOTTOM" -> ToolbarLayout.BOTTOM
            "TOP" -> ToolbarLayout.TOP
            else -> ToolbarLayout.COMPACT
        }
    }

    val glassTint: Flow<Float> = context.dataStore.data.map { it[Keys.GLASS_TINT] ?: 0.5f }

    val reduceTransparency: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.REDUCE_TRANSPARENCY] ?: false
    }

    val fingerprintProfile: Flow<FingerprintProfile> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.FINGERPRINT_PROFILE]) {
            "BALANCED" -> FingerprintProfile.BALANCED
            "OFF" -> FingerprintProfile.OFF
            else -> FingerprintProfile.STRICT
        }
    }

    val isDarkThemeMode: Flow<String> = context.dataStore.data.map {
        it[Keys.DARK_THEME] ?: "system"
    }

    suspend fun setToolbarLayout(layout: ToolbarLayout) {
        context.dataStore.edit { it[Keys.LAYOUT] = layout.name }
    }

    suspend fun setGlassTint(value: Float) {
        context.dataStore.edit { it[Keys.GLASS_TINT] = value.coerceIn(0f, 1f) }
    }

    suspend fun setReduceTransparency(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REDUCE_TRANSPARENCY] = enabled }
    }

    suspend fun setFingerprintProfile(profile: FingerprintProfile) {
        context.dataStore.edit { it[Keys.FINGERPRINT_PROFILE] = profile.name }
    }
}
