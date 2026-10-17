package com.expedition.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ExpeditionApp
import com.expedition.browser.data.db.DownloadEntity
import com.expedition.browser.ui.theme.AccentBlueLight
import com.expedition.browser.ui.theme.SurfaceDark
import com.expedition.browser.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun DownloadsSheet(
    isDark: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as ExpeditionApp
    val downloads by app.database.downloadDao().getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val bg = if (isDark) Color.Black else Color(0xFFF2F2F7)
    val cardBg = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color.White else Color.Black

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
            Text("Downloads", color = textColor, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        if (downloads.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No downloads yet", color = TextSecondary, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(downloads, key = { it.id }) { item ->
                    DownloadRow(
                        item = item,
                        cardBg = cardBg,
                        textColor = textColor,
                        onDelete = {
                            scope.launch { app.database.downloadDao().delete(item.id) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadRow(
    item: DownloadEntity,
    cardBg: Color,
    textColor: Color,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Download, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.fileName,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.status.replaceFirstChar { it.uppercase() },
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
        if (item.status == "running" && item.progress in 0..99) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { item.progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = AccentBlueLight
            )
        }
    }
}
