package com.expedition.browser.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.theme.AccentBlueLight
import com.expedition.browser.ui.theme.TextSecondary

@Composable
fun FindOnPageBar(
    matchCount: Int,
    currentMatch: Int,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    onQueryChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        variant = GlassVariant.REGULAR,
        cornerRadius = 16.dp,
        isDark = isDark,
        reduceTransparency = reduceTransparency,
        glassTint = glassTint,
        scrollVelocity = 0f
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChange(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .padding(horizontal = 8.dp),
                textStyle = TextStyle(
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 16.sp
                ),
                singleLine = true,
                cursorBrush = SolidColor(AccentBlueLight),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onNext() }),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Find on page", color = TextSecondary, fontSize = 16.sp)
                    }
                    inner()
                }
            )

            if (query.isNotEmpty()) {
                Text(
                    text = if (matchCount == 0) "0" else "$currentMatch / $matchCount",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            IconButton(onClick = onPrevious, enabled = matchCount > 0, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous",
                    tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = onNext, enabled = matchCount > 0, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next",
                    tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Done",
                    tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(20.dp))
            }
        }
    }
}
