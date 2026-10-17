package com.expedition.browser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.glass.GlassSurface
import com.expedition.browser.ui.glass.GlassVariant
import com.expedition.browser.ui.theme.AccentBlueLight
import com.expedition.browser.ui.theme.TextSecondary
import java.net.URI

@Composable
fun AddressCapsule(
    url: String,
    title: String,
    isLoading: Boolean,
    progress: Int,
    isSecure: Boolean,
    isFocused: Boolean,
    isDark: Boolean,
    glassTint: Float,
    reduceTransparency: Boolean,
    scrollVelocity: Float,
    onFocusChange: (Boolean) -> Unit,
    onUrlSubmit: (String) -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onPageControlsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var text by remember(url) { mutableStateOf(url) }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            text = url
            focusRequester.requestFocus()
            keyboard?.show()
        } else {
            keyboard?.hide()
        }
    }

    val domain = remember(url) {
        try {
            URI(url).host?.removePrefix("www.") ?: url
        } catch (_: Exception) {
            url.ifEmpty { "Search or enter URL" }
        }
    }

    GlassSurface(
        modifier = modifier.height(50.dp),
        variant = GlassVariant.REGULAR,
        cornerRadius = 25.dp,
        isDark = isDark,
        reduceTransparency = reduceTransparency,
        glassTint = glassTint,
        scrollVelocity = scrollVelocity
    ) {
        Box(Modifier.fillMaxSize()) {
            if (isFocused) {
                // Focused editing mode
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged { if (!it.isFocused && isFocused) onFocusChange(false) },
                        textStyle = TextStyle(
                            color = if (isDark) Color.White else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(AccentBlueLight),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                onUrlSubmit(text)
                                onFocusChange(false)
                            }
                        ),
                        decorationBox = { inner ->
                            if (text.isEmpty()) {
                                Text(
                                    "Search or enter URL",
                                    color = TextSecondary,
                                    fontSize = 16.sp
                                )
                            }
                            inner()
                        }
                    )

                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = { text = "" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = "Cancel",
                        color = AccentBlueLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable {
                                onFocusChange(false)
                                text = url
                            }
                    )
                }
            } else {
                // Idle mode
                Row(
                    Modifier
                        .fillMaxSize()
                        .clickable { onFocusChange(true) }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page controls glyph (A / A)
                    Text(
                        text = "Aᴬ",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onPageControlsClick() }
                            .padding(end = 8.dp)
                    )

                    // Lock / Not Secure
                    if (url.isNotEmpty() && url != "about:blank") {
                        Icon(
                            imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isSecure) TextSecondary else Color(0xFFFF3B30),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    }

                    Text(
                        text = domain,
                        color = if (isDark) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { if (isLoading) onStop() else onReload() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = if (isLoading) "Stop" else "Reload",
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Progress line along bottom rim
            if (isLoading && progress in 1..99) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(progress / 100f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp))
                        .background(AccentBlueLight)
                )
            }
        }
    }
}
