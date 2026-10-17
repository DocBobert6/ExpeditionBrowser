package com.expedition.browser.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.viewmodel.TabState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn

/**
 * Bottom layout: full-width address capsule stacked above a 5-button glass pill.
 */
@Composable
fun BottomToolbar(
    tab: TabState?,
    tabCount: Int,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isAddressFocused: Boolean,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    collapsed: Boolean,
    scrollVelocity: Float,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onShare: () -> Unit,
    onBookmarks: () -> Unit,
    onTabs: () -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onAddressFocusChange: (Boolean) -> Unit,
    onUrlSubmit: (String) -> Unit,
    onPageControlsClick: () -> Unit,
    onExpandChrome: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (collapsed && !isAddressFocused) {
        AddressCapsule(
            url = tab?.url ?: "",
            title = tab?.title ?: "",
            isLoading = tab?.isLoading == true,
            progress = tab?.progress ?: 0,
            isSecure = tab?.isSecure == true,
            isFocused = false,
            isDark = isDark,
            glassTint = glassTint,
            reduceTransparency = reduceTransparency,
            scrollVelocity = scrollVelocity,
            onFocusChange = {
                onExpandChrome()
                onAddressFocusChange(it)
            },
            onUrlSubmit = onUrlSubmit,
            onReload = onReload,
            onStop = onStop,
            onPageControlsClick = onPageControlsClick,
            modifier = modifier
                .height(40.dp)
                .widthIn(max = 220.dp)
        )
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AddressCapsule(
                url = tab?.url ?: "",
                title = tab?.title ?: "",
                isLoading = tab?.isLoading == true,
                progress = tab?.progress ?: 0,
                isSecure = tab?.isSecure == true,
                isFocused = isAddressFocused,
                isDark = isDark,
                glassTint = glassTint,
                reduceTransparency = reduceTransparency,
                scrollVelocity = scrollVelocity,
                onFocusChange = onAddressFocusChange,
                onUrlSubmit = onUrlSubmit,
                onReload = onReload,
                onStop = onStop,
                onPageControlsClick = onPageControlsClick,
                modifier = Modifier.fillMaxWidth()
            )

            if (!isAddressFocused) {
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    variant = GlassVariant.REGULAR,
                    cornerRadius = 26.dp,
                    isDark = isDark,
                    reduceTransparency = reduceTransparency,
                    glassTint = glassTint,
                    scrollVelocity = scrollVelocity
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ToolbarIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            enabled = canGoBack,
                            isDark = isDark,
                            onClick = onBack,
                            contentDescription = "Back"
                        )
                        ToolbarIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            enabled = canGoForward,
                            isDark = isDark,
                            onClick = onForward,
                            contentDescription = "Forward"
                        )
                        ToolbarIconButton(
                            icon = Icons.Default.Share,
                            enabled = true,
                            isDark = isDark,
                            onClick = onShare,
                            contentDescription = "Share"
                        )
                        ToolbarIconButton(
                            icon = Icons.Default.Bookmarks,
                            enabled = true,
                            isDark = isDark,
                            onClick = onBookmarks,
                            contentDescription = "Bookmarks"
                        )
                        // Tabs with count
                        Box(contentAlignment = Alignment.Center) {
                            ToolbarIconButton(
                                icon = Icons.Default.Tab,
                                enabled = true,
                                isDark = isDark,
                                onClick = onTabs,
                                contentDescription = "Tabs"
                            )
                            if (tabCount > 0) {
                                Text(
                                    text = if (tabCount > 9) "9+" else tabCount.toString(),
                                    color = if (isDark) Color.White else Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.offset(x = 8.dp, y = (-6).dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    enabled: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) {
                if (isDark) Color.White else Color.Black
            } else {
                Color.Gray.copy(alpha = 0.4f)
            },
            modifier = Modifier.size(24.dp)
        )
    }
}

