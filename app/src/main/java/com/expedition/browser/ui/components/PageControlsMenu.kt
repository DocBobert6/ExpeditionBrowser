package com.expedition.browser.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun PageControlsMenu(
    siteName: String,
    textZoomPercent: Int,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    onDismiss: () -> Unit,
    onTextZoomOut: () -> Unit,
    onTextZoomIn: () -> Unit,
    onReaderView: () -> Unit,
    onTranslate: () -> Unit,
    onRequestDesktop: () -> Unit,
    onWebsiteSettings: () -> Unit,
    onPrivacyReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        GlassSurface(
            modifier = modifier
                .widthIn(min = 280.dp, max = 340.dp)
                .padding(8.dp),
            variant = GlassVariant.REGULAR,
            cornerRadius = 16.dp,
            isDark = isDark,
            reduceTransparency = reduceTransparency,
            glassTint = glassTint,
            scrollVelocity = 0f
        ) {
            Column(Modifier.padding(vertical = 8.dp)) {
                // Header
                Text(
                    text = siteName.ifEmpty { "Page Controls" },
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider(
                    color = if (isDark) HairlineDark else HairlineLight,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Text size row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable(onClick = onTextZoomOut)
                            .padding(8.dp)
                    )
                    Text(
                        text = "$textZoomPercent%",
                        color = if (isDark) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "A",
                        color = if (isDark) Color.White else Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(onClick = onTextZoomIn)
                            .padding(8.dp)
                    )
                }

                HorizontalDivider(
                    color = if (isDark) HairlineDark else HairlineLight,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                PageControlRow("Show Reader View", Icons.Default.MenuBook, isDark, onReaderView)
                PageControlRow("Translate", Icons.Default.Language, isDark, onTranslate)
                PageControlRow("Request Desktop Website", Icons.Default.Computer, isDark, onRequestDesktop)
                PageControlRow("Website Settings", Icons.Default.Settings, isDark, onWebsiteSettings)
                PageControlRow("Privacy Report", Icons.Default.Shield, isDark, onPrivacyReport)
            }
        }
    }
}

@Composable
private fun PageControlRow(
    label: String,
    icon: ImageVector,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isDark) Color.White else Color.Black,
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
    }
}
