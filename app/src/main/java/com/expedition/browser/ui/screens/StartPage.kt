package com.expedition.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expedition.browser.ui.theme.AccentBlueLight
import com.expedition.browser.ui.theme.SurfaceDark
import com.expedition.browser.ui.theme.SurfaceLight
import com.expedition.browser.ui.theme.TextSecondary

data class FavoriteSite(
    val title: String,
    val url: String,
    val color: Color
)

@Composable
fun StartPage(
    isDark: Boolean,
    trackersBlocked: Int = 0,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color.Black else Color(0xFFF2F2F7)
    val cardBg = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    val favorites = listOf(
        FavoriteSite("Wikipedia", "https://wikipedia.org", Color(0xFF000000)),
        FavoriteSite("GitHub", "https://github.com", Color(0xFF24292F)),
        FavoriteSite("Reddit", "https://reddit.com", Color(0xFFFF4500)),
        FavoriteSite("YouTube", "https://youtube.com", Color(0xFFFF0000)),
        FavoriteSite("Maps", "https://maps.google.com", Color(0xFF34A853)),
        FavoriteSite("Weather", "https://weather.gov", Color(0xFF007AFF)),
        FavoriteSite("News", "https://news.ycombinator.com", Color(0xFFFF6600)),
        FavoriteSite("Translate", "https://translate.google.com", Color(0xFF4285F4))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 100.dp) // leave room for floating chrome
    ) {
        Text(
            text = "Expedition",
            color = textColor,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Favorites
        SectionHeader("Favorites", textColor)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // fixed height inside scroll
        ) {
            items(favorites) { site ->
                FavoriteItem(site = site, textColor = textColor) {
                    onOpenUrl(site.url)
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Privacy Report card
        SectionHeader("Privacy Report", textColor)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .clickable { /* open full report */ }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = AccentBlueLight,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "$trackersBlocked Trackers Prevented",
                        color = textColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "From Profiling You · Last 7 days",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Frequently Visited placeholder
        SectionHeader("Frequently Visited", textColor)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sites you visit often will appear here",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, textColor: Color) {
    Text(
        text = title,
        color = textColor,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun FavoriteItem(
    site: FavoriteSite,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(site.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = site.title.take(1).uppercase(),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = site.title,
            color = textColor,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
