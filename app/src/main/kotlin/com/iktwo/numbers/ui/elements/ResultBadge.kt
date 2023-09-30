package com.iktwo.numbers.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iktwo.numbers.model.state.InputState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ResultBadge(inputState: InputState, onAnimationFinished: @Composable () -> Unit) {
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = inputState == InputState.CORRECT,
            enter = slideInVertically {
                with(density) {
                    -100.dp.roundToPx()
                }
            } + fadeIn(initialAlpha = 0f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "+1",
                    fontSize = 64.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                )
            }

            if (transition.currentState == transition.targetState) {
                onAnimationFinished()
            }
        }
    }
}