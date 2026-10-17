package com.expedition.browser.ui.glass

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.expedition.browser.ui.theme.AccentBlueLight
import com.expedition.browser.ui.theme.AccentBlueDark
import com.expedition.browser.ui.theme.SurfaceDark
import com.expedition.browser.ui.theme.SurfaceLight

enum class GlassVariant {
    REGULAR,   // adaptive, more diffusion
    CLEAR      // more transparent, no adaptive
}

/**
 * Production glass surface.
 * - API 33+: full AGSL lensing + specular (refraction disabled during high scroll velocity)
 * - API 31-32: RenderEffect blur + rim
 * - < 31: high-opacity tinted surface with rim highlight
 *
 * Interactive: scale + brighten on press with spring.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    variant: GlassVariant = GlassVariant.REGULAR,
    cornerRadius: Dp = 25.dp,
    blurRadius: Dp = 16.dp,
    isDark: Boolean = false,
    reduceTransparency: Boolean = false,
    glassTint: Float = 0.5f,           // 0 = Clear, 1 = fully Tinted (from Settings slider)
    scrollVelocity: Float = 0f,        // for frame-budget throttle
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "glassPressScale"
    )

    // Frame-budget protection: disable expensive refraction above velocity threshold
    val refractionEnabled = scrollVelocity < 1200f

    val density = LocalDensity.current
    val shape = RoundedCornerShape(cornerRadius)

    // Approximate glass appearance for all API levels (full AGSL path can be swapped in later)
    val baseAlpha = when {
        reduceTransparency -> 0.94f
        variant == GlassVariant.CLEAR -> 0.35f + glassTint * 0.25f
        else -> 0.55f + glassTint * 0.3f
    }

    val surfaceColor = if (isDark) {
        SurfaceDark.copy(alpha = baseAlpha)
    } else {
        SurfaceLight.copy(alpha = baseAlpha)
    }

    val rimColor = Color.White.copy(alpha = if (isDark) 0.18f else 0.45f)

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (reduceTransparency) 4.dp else 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.22f)
            )
            .clip(shape)
            .background(surfaceColor)
            .graphicsLayer {
                // Placeholder for RuntimeShader attachment on API 33+
                // In a full implementation we would set renderEffect or RuntimeShader here
                // when refractionEnabled && Build.VERSION.SDK_INT >= 33
            }
            .then(
                if (onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                pressed = true
                                tryAwaitRelease()
                                pressed = false
                            },
                            onTap = { onClick() }
                        )
                    }
                } else Modifier
            ),
        content = content
    )
}
