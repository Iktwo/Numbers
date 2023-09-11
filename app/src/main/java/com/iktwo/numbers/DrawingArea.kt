package com.iktwo.numbers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.digitalink.Ink
import com.iktwo.numbers.ui.theme.Padding
import com.smarttoolfactory.gesture.MotionEvent
import com.smarttoolfactory.gesture.pointerMotionEvents

// Based on Thracian - https://stackoverflow.com/a/71090112
@Composable
fun DrawingArea(modifier: Modifier, recognize: (Ink) -> Unit) {
    var inkBuilder by remember {
        mutableStateOf(Ink.builder())
    }

    var strokeBuilder = Ink.Stroke.builder()

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var position by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var path by remember { mutableStateOf(Path()) }

    val canvasModifier = Modifier
        .fillMaxWidth()
        .clipToBounds()
        .background(Color.White)
        .pointerMotionEvents(onDown = { pointerInputChange: PointerInputChange ->
            position = pointerInputChange.position

            strokeBuilder = Ink.Stroke.builder()
            strokeBuilder.addPoint(
                Ink.Point.create(
                    position.x, position.y, System.currentTimeMillis()
                )
            )

            motionEvent = MotionEvent.Down
            pointerInputChange.consume()
        }, onMove = { pointerInputChange: PointerInputChange ->
            position = pointerInputChange.position
            motionEvent = MotionEvent.Move

            strokeBuilder.addPoint(
                Ink.Point.create(
                    position.x, position.y, System.currentTimeMillis()
                )
            )

            pointerInputChange.consume()
        }, onUp = { pointerInputChange: PointerInputChange ->
            motionEvent = MotionEvent.Up
            position = pointerInputChange.position

            strokeBuilder.addPoint(
                Ink.Point.create(
                    position.x, position.y, System.currentTimeMillis()
                )
            )

            inkBuilder.addStroke(strokeBuilder.build())

            val ink = inkBuilder.build()

            if (ink.strokes.isNotEmpty()) {
                recognize(ink)
            }

            pointerInputChange.consume()
        }, delayAfterDownInMillis = 25L
        )

    Box(modifier) {
        Canvas(
            modifier = canvasModifier.fillMaxSize()
        ) {
            when (motionEvent) {
                MotionEvent.Down -> {
                    path.moveTo(position.x, position.y)
                    previousPosition = position
                }

                MotionEvent.Move -> {
                    path.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + position.x) / 2,
                        (previousPosition.y + position.y) / 2

                    )
                    previousPosition = position
                }

                MotionEvent.Up -> {
                    path.lineTo(position.x, position.y)
                    position = Offset.Unspecified
                    previousPosition = position
                    motionEvent = MotionEvent.Idle
                }

                else -> Unit
            }

            drawPath(
                color = Color.DarkGray, path = path, alpha = 0.4f, style = Stroke(
                    width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round
                )
            )
        }

        Button(modifier = Modifier
            .align(TopCenter)
            .padding(Padding),
            onClick = {
                path = Path()
                strokeBuilder = Ink.Stroke.builder()
                inkBuilder = Ink.Builder()
            }) {

            // TODO: move this to resources and translate
            Text(text = "Erase")
        }
    }
}