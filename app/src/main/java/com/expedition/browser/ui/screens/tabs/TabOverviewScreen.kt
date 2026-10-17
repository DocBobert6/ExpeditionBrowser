package com.expedition.browser.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.theme.SurfaceDark
import com.expedition.browser.ui.theme.SurfaceLight
import com.expedition.browser.ui.theme.TextSecondary
import com.expedition.browser.ui.viewmodel.TabState

@Composable
fun TabOverviewScreen(
    tabs: List<TabState>,
    currentTabId: String,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color.Black else Color(0xFFF2F2F7)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header
            Text(
                text = "Tabs",
                color = if (isDark) Color.White else Color.Black,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tabs, key = { it.id }) { tab ->
                    TabCard(
                        tab = tab,
                        isSelected = tab.id == currentTabId,
                        isDark = isDark,
                        onClick = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }

            // Bottom floating controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassSurface(
                    modifier = Modifier.size(48.dp),
                    variant = GlassVariant.REGULAR,
                    cornerRadius = 24.dp,
                    isDark = isDark,
                    reduceTransparency = reduceTransparency,
                    glassTint = glassTint,
                    scrollVelocity = 0f,
                    onClick = onNewTab
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "New Tab",
                            tint = if (isDark) Color.White else Color.Black)
                    }
                }

                GlassSurface(
                    modifier = Modifier
                        .height(44.dp)
                        .widthIn(min = 100.dp),
                    variant = GlassVariant.REGULAR,
                    cornerRadius = 22.dp,
                    isDark = isDark,
                    reduceTransparency = reduceTransparency,
                    glassTint = glassTint,
                    scrollVelocity = 0f
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "${tabs.size} Tabs",
                            color = if (isDark) Color.White else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                GlassSurface(
                    modifier = Modifier
                        .height(44.dp)
                        .widthIn(min = 72.dp),
                    variant = GlassVariant.REGULAR,
                    cornerRadius = 22.dp,
                    isDark = isDark,
                    reduceTransparency = reduceTransparency,
                    glassTint = glassTint,
                    scrollVelocity = 0f,
                    onClick = onDone
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Done",
                            color = if (isDark) Color.White else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: TabState,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val cardBg = if (isDark) SurfaceDark else SurfaceLight

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .clickable(onClick = onClick)
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tab.title.ifEmpty { "New Tab" },
                color = if (isDark) Color.White else Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close tab",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = try {
                    java.net.URI(tab.url).host?.removePrefix("www.") ?: "New Tab"
                } catch (_: Exception) {
                    "New Tab"
                },
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

