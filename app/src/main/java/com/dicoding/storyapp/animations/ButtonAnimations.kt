package com.dicoding.storyapp.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    text: String,
    duration: Int = 150
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                scale.animateTo(
                    targetValue = 1.2f,
                    animationSpec = tween(durationMillis = 150)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 150)
                )
            }
            onClick()
        },
        modifier = Modifier
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            .padding(16.dp)
    ) {
        Text(text = text)
    }
}
