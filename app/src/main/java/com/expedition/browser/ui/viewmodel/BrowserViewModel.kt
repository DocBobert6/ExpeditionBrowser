package com.expedition.browser.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expedition.browser.ExpeditionApp
import com.expedition.browser.data.datastore.ToolbarLayout
import com.expedition.browser.engine.FingerprintProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TabState(
    val id: String,
    val url: String = "about:blank",
    val title: String = "New Tab",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isSecure: Boolean = false,
    val isPrivate: Boolean = false
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ExpeditionApp
    private val settings = app.settingsRepository

    private val _tabs = MutableStateFlow<List<TabState>>(listOf(TabState(id = "tab-1")))
    val tabs: StateFlow<List<TabState>> = _tabs.asStateFlow()

    private val _currentTabId = MutableStateFlow("tab-1")
    val currentTabId: StateFlow<String> = _currentTabId.asStateFlow()

    val currentTab: StateFlow<TabState?> = combine(_tabs, _currentTabId) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val toolbarLayout: StateFlow<ToolbarLayout> = settings.toolbarLayout
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToolbarLayout.COMPACT)

    val glassTint: StateFlow<Float> = settings.glassTint
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)

    val reduceTransparency: StateFlow<Boolean> = settings.reduceTransparency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val fingerprintProfile: StateFlow<FingerprintProfile> = settings.fingerprintProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FingerprintProfile.STRICT)

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _chromeCollapsed = MutableStateFlow(false)
    val chromeCollapsed: StateFlow<Boolean> = _chromeCollapsed.asStateFlow()

    private val _scrollVelocity = MutableStateFlow(0f)
    val scrollVelocity: StateFlow<Float> = _scrollVelocity.asStateFlow()

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun updateScrollVelocity(velocity: Float) {
        _scrollVelocity.value = velocity
    }

    fun setChromeCollapsed(collapsed: Boolean) {
        _chromeCollapsed.value = collapsed
    }

    fun loadUrl(url: String) {
        val id = _currentTabId.value
        _tabs.update { list ->
            list.map {
                if (it.id == id) it.copy(url = normalizeUrlPublic(url), isLoading = true, progress = 0)
                else it
            }
        }
    }

    fun onPageStarted(url: String) {
        val id = _currentTabId.value
        _tabs.update { list ->
            list.map {
                if (it.id == id) it.copy(url = url, isLoading = true, isSecure = url.startsWith("https"))
                else it
            }
        }
    }

    fun onPageFinished(url: String, title: String?) {
        val id = _currentTabId.value
        val resolvedTitle = title ?: ""
        _tabs.update { list ->
            list.map {
                if (it.id == id) it.copy(
                    url = url,
                    title = resolvedTitle.ifEmpty { it.title },
                    isLoading = false,
                    progress = 100,
                    isSecure = url.startsWith("https")
                ) else it
            }
        }
        _chromeCollapsed.value = false
        // Persist to history (skip blank / private)
        val tab = _tabs.value.find { it.id == id }
        if (tab != null && !tab.isPrivate && url.isNotEmpty() && url != "about:blank") {
            viewModelScope.launch {
                app.database.historyDao().insert(
                    com.expedition.browser.data.db.HistoryEntity(
                        title = resolvedTitle.ifEmpty { url },
                        url = url
                    )
                )
            }
        }
    }

    fun onProgress(progress: Int) {
        val id = _currentTabId.value
        _tabs.update { list ->
            list.map { if (it.id == id) it.copy(progress = progress) else it }
        }
    }

    fun newTab(private: Boolean = false) {
        val newId = "tab-${System.currentTimeMillis()}"
        _tabs.update { it + TabState(id = newId, isPrivate = private) }
        _currentTabId.value = newId
    }

    fun closeTab(tabId: String) {
        _tabs.update { list ->
            val next = list.filterNot { it.id == tabId }
            if (next.isEmpty()) listOf(TabState(id = "tab-${System.currentTimeMillis()}"))
            else next
        }
        if (_currentTabId.value == tabId) {
            _currentTabId.value = _tabs.value.first().id
        }
        app.tabHibernationManager.unregister(tabId)
    }

    fun switchTab(tabId: String) {
        if (_tabs.value.any { it.id == tabId }) {
            _currentTabId.value = tabId
        }
    }

    fun setToolbarLayout(layout: ToolbarLayout) {
        viewModelScope.launch { settings.setToolbarLayout(layout) }
    }

    fun normalizeUrlPublic(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "about:blank"
        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }
    }

    // ---- UI Navigation State ----
    enum class Overlay {
        None, MoreMenu, TabOverview, Settings, Bookmarks, History, FindOnPage, PageControls
    }

    private val _overlay = MutableStateFlow(Overlay.None)
    val overlay: StateFlow<Overlay> = _overlay.asStateFlow()

    fun showOverlay(overlay: Overlay) {
        _overlay.value = overlay
    }

    fun dismissOverlay() {
        _overlay.value = Overlay.None
    }

    fun setGlassTint(value: Float) {
        viewModelScope.launch { settings.setGlassTint(value) }
    }

    fun setReduceTransparency(enabled: Boolean) {
        viewModelScope.launch { settings.setReduceTransparency(enabled) }
    }

    fun setFingerprintProfile(profile: FingerprintProfile) {
        viewModelScope.launch { settings.setFingerprintProfile(profile) }
    }

    fun addBookmark() {
        val tab = currentTab.value ?: return
        if (tab.url.isEmpty() || tab.url == "about:blank") return
        viewModelScope.launch {
            app.database.bookmarkDao().insert(
                com.expedition.browser.data.db.BookmarkEntity(
                    title = tab.title.ifEmpty { tab.url },
                    url = tab.url
                )
            )
        }
    }

    private val _textZoomPercent = MutableStateFlow(100)
    val textZoomPercent: StateFlow<Int> = _textZoomPercent.asStateFlow()

    fun adjustTextZoom(delta: Int) {
        _textZoomPercent.value = (_textZoomPercent.value + delta).coerceIn(50, 300)
    }

    private val _findMatchCount = MutableStateFlow(0)
    val findMatchCount: StateFlow<Int> = _findMatchCount.asStateFlow()
    private val _findCurrentMatch = MutableStateFlow(0)
    val findCurrentMatch: StateFlow<Int> = _findCurrentMatch.asStateFlow()

    fun setFindMatches(count: Int, current: Int) {
        _findMatchCount.value = count
        _findCurrentMatch.value = current
    }
}
