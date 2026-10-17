package com.expedition.browser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.viewmodel.TabState

@Composable
fun CompactToolbar(
    tab: TabState?,
    canGoBack: Boolean,
    canGoForward: Boolean,
    showForward: Boolean,
    isAddressFocused: Boolean,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    collapsed: Boolean,
    scrollVelocity: Float,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onMore: () -> Unit,
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
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = !isAddressFocused,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassCircleButton(
                        onClick = onBack,
                        enabled = canGoBack,
                        isDark = isDark,
                        glassTint = glassTint,
                        reduceTransparency = reduceTransparency,
                        scrollVelocity = scrollVelocity
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    AnimatedVisibility(visible = showForward) {
                        GlassCircleButton(
                            onClick = onForward,
                            enabled = canGoForward,
                            isDark = isDark,
                            glassTint = glassTint,
                            reduceTransparency = reduceTransparency,
                            scrollVelocity = scrollVelocity
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Forward",
                                tint = if (isDark) Color.White else Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

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
                modifier = Modifier.weight(1f)
            )

            AnimatedVisibility(
                visible = !isAddressFocused,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                GlassCircleButton(
                    onClick = onMore,
                    enabled = true,
                    isDark = isDark,
                    glassTint = glassTint,
                    reduceTransparency = reduceTransparency,
                    scrollVelocity = scrollVelocity
                ) {
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCircleButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    scrollVelocity: Float,
    content: @Composable BoxScope.() -> Unit
) {
    GlassSurface(
        modifier = Modifier.size(50.dp),
        variant = GlassVariant.REGULAR,
        cornerRadius = 25.dp,
        isDark = isDark,
        reduceTransparency = reduceTransparency,
        glassTint = glassTint,
        scrollVelocity = scrollVelocity,
        onClick = if (enabled) onClick else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
