package com.expedition.browser.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.expedition.browser.data.datastore.ToolbarLayout
import com.expedition.browser.engine.webview.ExpeditionWebView
import com.expedition.browser.ui.components.CompactToolbar
import com.expedition.browser.ui.components.BottomToolbar
import com.expedition.browser.ui.components.TopToolbar
import com.expedition.browser.ui.components.PageControlsMenu
import com.expedition.browser.ui.components.FindOnPageBar
import com.expedition.browser.ui.components.MoreMenu
import com.expedition.browser.ui.screens.tabs.TabOverviewScreen
import com.expedition.browser.ui.screens.settings.SettingsScreen
import com.expedition.browser.ui.screens.StartPage
import com.expedition.browser.ui.screens.HistorySheet
import com.expedition.browser.ui.screens.BookmarksSheet
import com.expedition.browser.ui.viewmodel.BrowserViewModel

@Composable
fun BrowserScreen(viewModel: BrowserViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val currentTabId by viewModel.currentTabId.collectAsState()
    val layout by viewModel.toolbarLayout.collectAsState()
    val glassTint by viewModel.glassTint.collectAsState()
    val reduceTransparency by viewModel.reduceTransparency.collectAsState()
    val fingerprintProfile by viewModel.fingerprintProfile.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val chromeCollapsed by viewModel.chromeCollapsed.collectAsState()
    val scrollVelocity by viewModel.scrollVelocity.collectAsState()
    val overlay by viewModel.overlay.collectAsState()
    val findMatchCount by viewModel.findMatchCount.collectAsState()
    val findCurrentMatch by viewModel.findCurrentMatch.collectAsState()
    val textZoomPercent by viewModel.textZoomPercent.collectAsState()

    var isAddressFocused by remember { mutableStateOf(false) }
    var showForward by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<ExpeditionWebView?>(null) }

    val canGoBack = webViewRef?.canGoBack() == true
    val canGoForward = webViewRef?.canGoForward() == true

    LaunchedEffect(canGoBack) {
        if (canGoBack) showForward = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Content: Start Page or WebView
        currentTab?.let { tab ->
            if (tab.url.isEmpty() || tab.url == "about:blank") {
                StartPage(
                    isDark = isDark,
                    trackersBlocked = 0,
                    onOpenUrl = { url ->
                        viewModel.loadUrl(url)
                        webViewRef?.navigate(url)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AndroidView(
                    factory = { ctx ->
                        ExpeditionWebView(ctx).apply {
                            onPageStartedCallback = { url -> viewModel.onPageStarted(url) }
                            onPageFinishedCallback = { url, title ->
                                viewModel.onPageFinished(url, title)
                            }
                            onProgressCallback = { progress -> viewModel.onProgress(progress) }
                            onScrollChangedCallback = { scrollY, delta ->
                                if (delta > 8 && scrollY > 80) {
                                    viewModel.setChromeCollapsed(true)
                                } else if (delta < -8) {
                                    viewModel.setChromeCollapsed(false)
                                }
                                viewModel.updateScrollVelocity(kotlin.math.abs(delta).toFloat() * 60f)
                            }
                            applyFingerprintProfile(fingerprintProfile)
                            navigate(tab.url)
                            webViewRef = this
                        }
                    },
                    update = { wv ->
                        wv.applyFingerprintProfile(fingerprintProfile)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating chrome (hidden when Tab Overview / Settings open)
        if (overlay != BrowserViewModel.Overlay.TabOverview &&
            overlay != BrowserViewModel.Overlay.Settings) {

            val bottomMod = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp)
            val topMod = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)

            when (layout) {
                ToolbarLayout.BOTTOM -> BottomToolbar(
                    tab = currentTab,
                    tabCount = tabs.size,
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    isAddressFocused = isAddressFocused,
                    isDark = isDark,
                    glassTint = glassTint,
                    reduceTransparency = reduceTransparency,
                    collapsed = chromeCollapsed,
                    scrollVelocity = scrollVelocity,
                    onBack = { webViewRef?.goBack(); showForward = true },
                    onForward = { webViewRef?.goForward() },
                    onShare = { },
                    onBookmarks = { viewModel.showOverlay(BrowserViewModel.Overlay.Bookmarks) },
                    onTabs = { viewModel.showOverlay(BrowserViewModel.Overlay.TabOverview) },
                    onReload = { webViewRef?.reload() },
                    onStop = { webViewRef?.stopLoading() },
                    onAddressFocusChange = { focused ->
                        isAddressFocused = focused
                        if (focused) viewModel.setChromeCollapsed(false)
                    },
                    onUrlSubmit = { input ->
                        val normalized = viewModel.normalizeUrlPublic(input)
                        viewModel.loadUrl(normalized)
                        webViewRef?.navigate(normalized)
                        isAddressFocused = false
                    },
                    onPageControlsClick = { viewModel.showOverlay(BrowserViewModel.Overlay.PageControls) },
                    onExpandChrome = { viewModel.setChromeCollapsed(false) },
                    modifier = bottomMod
                )
                ToolbarLayout.TOP -> TopToolbar(
                    tab = currentTab,
                    tabCount = tabs.size,
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    isAddressFocused = isAddressFocused,
                    isDark = isDark,
                    glassTint = glassTint,
                    reduceTransparency = reduceTransparency,
                    collapsed = chromeCollapsed,
                    scrollVelocity = scrollVelocity,
                    onBack = { webViewRef?.goBack(); showForward = true },
                    onForward = { webViewRef?.goForward() },
                    onShare = { },
                    onBookmarks = { viewModel.showOverlay(BrowserViewModel.Overlay.Bookmarks) },
                    onTabs = { viewModel.showOverlay(BrowserViewModel.Overlay.TabOverview) },
                    onReload = { webViewRef?.reload() },
                    onStop = { webViewRef?.stopLoading() },
                    onAddressFocusChange = { focused ->
                        isAddressFocused = focused
                        if (focused) viewModel.setChromeCollapsed(false)
                    },
                    onUrlSubmit = { input ->
                        val normalized = viewModel.normalizeUrlPublic(input)
                        viewModel.loadUrl(normalized)
                        webViewRef?.navigate(normalized)
                        isAddressFocused = false
                    },
                    onPageControlsClick = { viewModel.showOverlay(BrowserViewModel.Overlay.PageControls) },
                    onExpandChrome = { viewModel.setChromeCollapsed(false) },
                    topModifier = topMod,
                    bottomModifier = bottomMod
                )
                else -> CompactToolbar(
                    tab = currentTab,
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    showForward = showForward && canGoForward,
                    isAddressFocused = isAddressFocused,
                    isDark = isDark,
                    glassTint = glassTint,
                    reduceTransparency = reduceTransparency,
                    collapsed = chromeCollapsed,
                    scrollVelocity = scrollVelocity,
                    onBack = { webViewRef?.goBack(); showForward = true },
                    onForward = { webViewRef?.goForward() },
                    onReload = { webViewRef?.reload() },
                    onStop = { webViewRef?.stopLoading() },
                    onMore = { viewModel.showOverlay(BrowserViewModel.Overlay.MoreMenu) },
                    onAddressFocusChange = { focused ->
                        isAddressFocused = focused
                        if (focused) viewModel.setChromeCollapsed(false)
                    },
                    onUrlSubmit = { input ->
                        val normalized = viewModel.normalizeUrlPublic(input)
                        viewModel.loadUrl(normalized)
                        webViewRef?.navigate(normalized)
                        isAddressFocused = false
                    },
                    onPageControlsClick = { viewModel.showOverlay(BrowserViewModel.Overlay.PageControls) },
                    onExpandChrome = { viewModel.setChromeCollapsed(false) },
                    modifier = bottomMod
                )
            }
        }

        // More menu popup
        if (overlay == BrowserViewModel.Overlay.MoreMenu) {
            MoreMenu(
                isDark = isDark,
                glassTint = glassTint,
                reduceTransparency = reduceTransparency,
                onDismiss = { viewModel.dismissOverlay() },
                onNewTab = {
                    viewModel.newTab()
                    viewModel.dismissOverlay()
                },
                onShare = { viewModel.dismissOverlay() },
                onAddBookmark = { viewModel.addBookmark(); viewModel.dismissOverlay() },
                onAddReadingList = { viewModel.dismissOverlay() },
                onAllTabs = {
                    viewModel.showOverlay(BrowserViewModel.Overlay.TabOverview)
                },
                onBookmarks = { viewModel.showOverlay(BrowserViewModel.Overlay.Bookmarks) },
                onHistory = { viewModel.showOverlay(BrowserViewModel.Overlay.History) },
                onFindOnPage = { viewModel.showOverlay(BrowserViewModel.Overlay.FindOnPage) },
                onRequestDesktop = { viewModel.dismissOverlay() },
                onSettings = {
                    viewModel.showOverlay(BrowserViewModel.Overlay.Settings)
                }
            )
        }

        // Tab Overview (full screen)
        if (overlay == BrowserViewModel.Overlay.TabOverview) {
            TabOverviewScreen(
                tabs = tabs,
                currentTabId = currentTabId,
                isDark = isDark,
                glassTint = glassTint,
                reduceTransparency = reduceTransparency,
                onSelectTab = { id ->
                    viewModel.switchTab(id)
                    viewModel.dismissOverlay()
                },
                onCloseTab = { id -> viewModel.closeTab(id) },
                onNewTab = {
                    viewModel.newTab()
                    viewModel.dismissOverlay()
                },
                onDone = { viewModel.dismissOverlay() },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Settings
        if (overlay == BrowserViewModel.Overlay.Settings) {
            SettingsScreen(
                viewModel = viewModel,
                isDark = isDark,
                onBack = { viewModel.dismissOverlay() },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bookmarks
        if (overlay == BrowserViewModel.Overlay.Bookmarks) {
            BookmarksSheet(
                isDark = isDark,
                onOpenUrl = { url ->
                    viewModel.loadUrl(url)
                    webViewRef?.navigate(url)
                    viewModel.dismissOverlay()
                },
                onBack = { viewModel.dismissOverlay() },
                modifier = Modifier.fillMaxSize()
            )
        }

        // History
        if (overlay == BrowserViewModel.Overlay.History) {
            HistorySheet(
                isDark = isDark,
                onOpenUrl = { url ->
                    viewModel.loadUrl(url)
                    webViewRef?.navigate(url)
                    viewModel.dismissOverlay()
                },
                onBack = { viewModel.dismissOverlay() },
                modifier = Modifier.fillMaxSize()
            )
        }

    }

    // Overlay layer
    Box(modifier = Modifier.fillMaxSize()) {
        // Find on Page bar
        if (overlay == BrowserViewModel.Overlay.FindOnPage) {
            FindOnPageBar(
                matchCount = findMatchCount,
                currentMatch = findCurrentMatch,
                isDark = isDark,
                glassTint = glassTint,
                reduceTransparency = reduceTransparency,
                onQueryChange = { q ->
                    webViewRef?.runFind(q)
                },
                onNext = { webViewRef?.runFindNext(true) },
                onPrevious = { webViewRef?.runFindNext(false) },
                onDismiss = {
                    webViewRef?.runClearMatches()
                    viewModel.dismissOverlay()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }

        // Page Controls menu
        if (overlay == BrowserViewModel.Overlay.PageControls) {
            val site = try {
                java.net.URI(currentTab?.url ?: "").host?.removePrefix("www.") ?: "Page"
            } catch (_: Exception) { "Page" }
            PageControlsMenu(
                siteName = site,
                textZoomPercent = textZoomPercent,
                isDark = isDark,
                glassTint = glassTint,
                reduceTransparency = reduceTransparency,
                onDismiss = { viewModel.dismissOverlay() },
                onTextZoomOut = {
                    viewModel.adjustTextZoom(-10)
                    webViewRef?.setTextZoom((textZoomPercent - 10).coerceIn(50, 300))
                },
                onTextZoomIn = {
                    viewModel.adjustTextZoom(10)
                    webViewRef?.setTextZoom((textZoomPercent + 10).coerceIn(50, 300))
                },
                onReaderView = { viewModel.dismissOverlay() },
                onTranslate = { viewModel.dismissOverlay() },
                onRequestDesktop = { viewModel.dismissOverlay() },
                onWebsiteSettings = { viewModel.dismissOverlay() },
                onPrivacyReport = { viewModel.dismissOverlay() }
            )
        }
    }

        DisposableEffect(Unit) {
        onDispose { webViewRef = null }
    }
}
