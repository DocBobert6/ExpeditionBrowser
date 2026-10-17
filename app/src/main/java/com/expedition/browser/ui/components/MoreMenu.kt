package com.expedition.browser.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.theme.HairlineDark
import com.expedition.browser.ui.theme.HairlineLight
import com.expedition.browser.ui.theme.TextSecondary

data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun MoreMenu(
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    onDismiss: () -> Unit,
    onNewTab: () -> Unit,
    onShare: () -> Unit,
    onAddBookmark: () -> Unit,
    onAddReadingList: () -> Unit,
    onAllTabs: () -> Unit,
    onBookmarks: () -> Unit,
    onHistory: () -> Unit,
    onFindOnPage: () -> Unit,
    onRequestDesktop: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        MenuItem("New Tab", Icons.Default.Add, onNewTab),
        MenuItem("Share", Icons.Default.Share, onShare),
        MenuItem("Add Bookmark", Icons.Default.BookmarkAdd, onAddBookmark),
        MenuItem("Add to Reading List", Icons.Default.MenuBook, onAddReadingList),
        MenuItem("All Tabs", Icons.Default.Tab, onAllTabs),
        MenuItem("Bookmarks", Icons.Default.Bookmarks, onBookmarks),
        MenuItem("History", Icons.Default.History, onHistory),
        MenuItem("Find on Page", Icons.Default.Search, onFindOnPage),
        MenuItem("Request Desktop Website", Icons.Default.Computer, onRequestDesktop),
        MenuItem("Settings", Icons.Default.Settings, onSettings)
    )

    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        GlassSurface(
            modifier = modifier
                .widthIn(min = 260.dp, max = 320.dp)
                .padding(8.dp),
            variant = GlassVariant.REGULAR,
            cornerRadius = 16.dp,
            isDark = isDark,
            reduceTransparency = reduceTransparency,
            glassTint = glassTint,
            scrollVelocity = 0f
        ) {
            Column(Modifier.padding(vertical = 8.dp)) {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                item.onClick()
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.label,
                            color = if (isDark) Color.White else Color.Black,
                            fontSize = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (index == 3 || index == 6) {
                        HorizontalDivider(
                            color = if (isDark) HairlineDark else HairlineLight,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
