package com.expedition.browser.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.data.datastore.ToolbarLayout
import com.expedition.browser.engine.FingerprintProfile
import com.expedition.browser.ui.theme.AccentBlueLight
import com.expedition.browser.ui.theme.SurfaceDark
import com.expedition.browser.ui.theme.SurfaceLight
import com.expedition.browser.ui.theme.TextSecondary
import com.expedition.browser.ui.viewmodel.BrowserViewModel

@Composable
fun SettingsScreen(
    viewModel: BrowserViewModel,
    isDark: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val layout by viewModel.toolbarLayout.collectAsState()
    val glassTint by viewModel.glassTint.collectAsState()
    val reduceTransparency by viewModel.reduceTransparency.collectAsState()
    val fingerprintProfile by viewModel.fingerprintProfile.collectAsState()

    val bg = if (isDark) Color.Black else Color(0xFFF2F2F7)
    val cardBg = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor
                )
            }
            Text(
                text = "Settings",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Layout section
            SettingsSection(title = "Layout", textColor = textColor) {
                SettingsCard(cardBg = cardBg) {
                    ToolbarLayout.entries.forEach { option ->
                        SettingsRow(
                            label = option.name.lowercase().replaceFirstChar { it.uppercase() },
                            selected = layout == option,
                            textColor = textColor,
                            onClick = { viewModel.setToolbarLayout(option) }
                        )
                    }
                }
            }

            // Glass section
            SettingsSection(title = "Appearance", textColor = textColor) {
                SettingsCard(cardBg = cardBg) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Glass",
                            color = textColor,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Clear  →  Tinted",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                        Slider(
                            value = glassTint,
                            onValueChange = { viewModel.setGlassTint(it) },
                            valueRange = 0f..1f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Privacy section
            SettingsSection(title = "Privacy", textColor = textColor) {
                SettingsCard(cardBg = cardBg) {
                    Text(
                        text = "Fingerprinting Protection",
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                    FingerprintProfile.entries.forEach { profile ->
                        SettingsRow(
                            label = profile.name.lowercase().replaceFirstChar { it.uppercase() },
                            selected = fingerprintProfile == profile,
                            textColor = textColor,
                            onClick = { viewModel.setFingerprintProfile(profile) }
                        )
                    }
                }
            }

            // About
            SettingsSection(title = "About", textColor = textColor) {
                SettingsCard(cardBg = cardBg) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Expedition Browser", color = textColor, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                        Text("Version 1.0.0", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    textColor: Color,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsCard(
    cardBg: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    label: String,
    selected: Boolean,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = AccentBlueLight,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
