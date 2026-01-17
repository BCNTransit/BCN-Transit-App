package com.example.bcntransit.BCNTransitApp.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.bcntransit.app.R

@Composable
fun CustomSplashScreen(
    onAnimationFinished: () -> Unit
) {
    val bgRed = Color(0xFFA81829)
    val bgGrey = Color(0xFF37474F)
    val bgWhite = Color(0xFFF5F5F5)
    val initialBgColor = Color.Black

    val animDuration = 1500
    val mainProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        mainProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = animDuration,
                easing = FastOutSlowInEasing
            )
        )
        delay(500)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(initialBgColor)
            .drawBehind {
                val halfWidth = size.width / 2
                val halfHeight = size.height / 2
                val bgProgress = getSegmentProgress(mainProgress.value, 0.0f, 0.4f)
                val entryOffset = 1f - bgProgress

                val redY = -halfHeight * entryOffset
                drawRect(color = bgRed, topLeft = Offset(0f, redY), size = Size(halfWidth, halfHeight))

                val whiteY = halfHeight + (halfHeight * entryOffset)
                drawRect(color = bgWhite, topLeft = Offset(0f, whiteY), size = Size(halfWidth, halfHeight))

                val greyX = halfWidth + (halfWidth * entryOffset)
                drawRect(color = bgGrey, topLeft = Offset(greyX, 0f), size = Size(halfWidth, size.height))
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logoProgress = getSegmentProgress(mainProgress.value, 0.4f, 0.9f)
            val scale = lerpFloat(0.8f, 1.8f, logoProgress)
            val alpha = logoProgress.coerceIn(0f, 1f)
            val cornerRadius = 32.dp

            Image(
                painter = painterResource(id = R.drawable.bcn_transit_2),
                contentDescription = "Logo BCN Transit",
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(50.dp))

            val textProgress = getSegmentProgress(mainProgress.value, 0.7f, 1.0f)
            val textAlpha = textProgress
            val textOffset = 20.dp * (1 - textProgress)

            Text(
                text = "BCN TRANSIT",
                modifier = Modifier
                    .offset(y = textOffset)
                    .alpha(textAlpha),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset)
            ) {
                Text(
                    text = "MOVE SMARTER",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// Helpers (igual que antes)
fun getSegmentProgress(globalProgress: Float, startFrame: Float, endFrame: Float): Float {
    val totalDuration = endFrame - startFrame
    if (totalDuration <= 0) return 0f
    val localProgress = (globalProgress - startFrame) / totalDuration
    return localProgress.coerceIn(0f, 1f)
}

fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
    return (start + (stop - start) * fraction)
}