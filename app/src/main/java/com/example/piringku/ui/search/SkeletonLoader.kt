package com.example.piringku.ui.search

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate",
    )
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFF2F4F0),
            Color(0xFFE1E3DF),
            Color(0xFFF2F4F0),
        ),
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim + 300f, 0f),
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
) {
    val brush = ShimmerBrush()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush),
    )
}

@Composable
fun SkeletonItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBox(modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.width(16.dp))
        ShimmerBox(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        ShimmerBox(modifier = Modifier.size(48.dp, 24.dp))
    }
}

@Composable
fun SkeletonLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        repeat(5) {
            SkeletonItem()
        }
    }
}
