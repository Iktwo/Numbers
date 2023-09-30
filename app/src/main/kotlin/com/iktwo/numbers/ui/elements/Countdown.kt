package com.iktwo.numbers.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun Countdown(
    modifier: Modifier = Modifier,
    initialTime: Int = 3,
    onFinish: () -> Unit
) {
    var time by remember { mutableStateOf(initialTime) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(time) {
        while (time > 0) {
            delay(1000L)
            time--
        }

        onFinish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        val textColor = MaterialTheme.colorScheme.onSurface

        Box(modifier = Modifier
            .background(Color.Blue)

            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)

//            .width(IntrinsicSize.Min)
            .padding(20.dp)

            .drawBehind {
                drawArc(
                    color = textColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(8.dp.toPx())
                )
            }) {

            Text(
                text = if (time > 0) time.toString() else "0",
                color = textColor,
                fontSize = 64.sp,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .aspectRatio(1f)
            )
        }

    }
}
