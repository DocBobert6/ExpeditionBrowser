package com.expedition.browser.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.theme.AccentBlueLight

/**
 * Shows "Paste and Go" when clipboard contains something that looks like a URL.
 */
@Composable
fun PasteAndGoChip(
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    onPasteAndGo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var clipUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()?.trim()
        clipUrl = if (text != null && looksLikeUrl(text)) text else null
    }

    clipUrl?.let { url ->
        GlassSurface(
            modifier = modifier,
            variant = GlassVariant.REGULAR,
            cornerRadius = 16.dp,
            isDark = isDark,
            reduceTransparency = reduceTransparency,
            glassTint = glassTint,
            scrollVelocity = 0f,
            onClick = { onPasteAndGo(url) }
        ) {
            Text(
                text = "Paste and Go",
                color = AccentBlueLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

private fun looksLikeUrl(text: String): Boolean {
    val t = text.lowercase()
    return t.startsWith("http://") || t.startsWith("https://") ||
        (t.contains(".") && !t.contains(" ") && t.length in 4..200)
}
