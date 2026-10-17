package com.expedition.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ExpeditionApp
import com.expedition.browser.data.db.HistoryEntity
import com.expedition.browser.ui.theme.SurfaceDark
import com.expedition.browser.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorySheet(
    isDark: Boolean,
    onOpenUrl: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as ExpeditionApp
    val history by app.database.historyDao().getRecent().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val bg = if (isDark) Color.Black else Color(0xFFF2F2F7)
    val cardBg = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val timeFmt = SimpleDateFormat("MMM d · h:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
            }
            Text(
                text = "History",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Clear",
                color = Color(0xFFFF3B30),
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        scope.launch { app.database.historyDao().clearAll() }
                    }
            )
        }

        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No history yet", color = TextSecondary, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history, key = { it.id }) { entry ->
                    HistoryRow(
                        entry = entry,
                        timeLabel = timeFmt.format(Date(entry.visitedAt)),
                        cardBg = cardBg,
                        textColor = textColor,
                        onClick = { onOpenUrl(entry.url) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntity,
    timeLabel: String,
    cardBg: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = entry.title.ifEmpty { entry.url },
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = timeLabel,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}
