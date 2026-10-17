package com.expedition.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.theme.TextSecondary

enum class ReaderBackground { WHITE, SEPIA, GRAY, BLACK }

@Composable
fun ReaderViewScreen(
    title: String,
    bodyText: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bgMode by remember { mutableStateOf(ReaderBackground.WHITE) }
    var fontSize by remember { mutableStateOf(18) }

    val bg = when (bgMode) {
        ReaderBackground.WHITE -> Color.White
        ReaderBackground.SEPIA -> Color(0xFFF4ECD8)
        ReaderBackground.GRAY -> Color(0xFF2C2C2E)
        ReaderBackground.BLACK -> Color.Black
    }
    val textColor = when (bgMode) {
        ReaderBackground.WHITE, ReaderBackground.SEPIA -> Color.Black
        else -> Color.White
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
            }
            Text(
                text = "Reader View",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            // Background toggles
            listOf(
                ReaderBackground.WHITE to "W",
                ReaderBackground.SEPIA to "S",
                ReaderBackground.GRAY to "G",
                ReaderBackground.BLACK to "B"
            ).forEach { (mode, label) ->
                Text(
                    text = label,
                    color = if (bgMode == mode) textColor else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clickable { bgMode = mode }
                )
            }
            Text(
                text = "A−",
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { fontSize = (fontSize - 2).coerceAtLeast(12) }
            )
            Text(
                text = "A+",
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { fontSize = (fontSize + 2).coerceAtMost(32) }
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = (fontSize + 6).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = bodyText.ifEmpty { "Unable to extract readable content from this page." },
                color = textColor,
                fontSize = fontSize.sp,
                lineHeight = (fontSize * 1.5).sp
            )
        }
    }
}

