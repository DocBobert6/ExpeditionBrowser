package com.expedition.browser.ui.glass

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Unified glass morph container.
 * During transitions the content is drawn under a single graphicsLayer so an AGSL
 * shader can perform smooth-union SDF morphing of multiple glass shapes.
 * Outside transitions, normal Compose hierarchy is used for performance.
 */
@Composable
fun GlassMorphContainer(
    modifier: Modifier = Modifier,
    isMorphing: Boolean,
    morphProgress: Float,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(morphProgress) }

    LaunchedEffect(morphProgress) {
        animatedProgress.animateTo(
            targetValue = morphProgress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    // On API 33+ we can attach RuntimeShader; fallback is simple graphicsLayer.
    val useAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    Box(
        modifier = modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                // Subtle scale during interactive press is handled by child InteractiveGlass
            }
            .drawWithContent {
                // Actual AGSL application happens in GlassSurface when a RuntimeShader is attached.
                // Here we ensure the offscreen layer is ready for backdrop capture.
                drawContent()
            }
    ) {
        content()
    }
}
