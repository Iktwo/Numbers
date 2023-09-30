package com.iktwo.numbers.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.iktwo.numbers.model.state.InputState
import com.iktwo.numbers.model.Operands
import com.iktwo.numbers.ui.elements.DrawingArea
import com.iktwo.numbers.ui.elements.DrawingAreaHandler
import com.iktwo.numbers.ui.elements.ResultBadge
import com.iktwo.numbers.ui.theme.LocalColors
import com.iktwo.numbers.ui.theme.Padding

// TODO: build mechanism to track score
@Composable
fun PageSums(
    inputState: InputState,
    operands: Operands,
    drawingAreaHandler: DrawingAreaHandler,
    onGenerateNewBoard: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(65f)
                    .background(
                        when (inputState) {
                            InputState.READY_FOR_INPUT -> {
                                LocalColors.current.inputReadyColor
                            }

                            InputState.INCORRECT -> {
                                LocalColors.current.inputFailureColor
                            }

                            InputState.CORRECT -> {
                                LocalColors.current.inputSuccessColor
                            }
                        }
                    )
                    .padding(Padding)
            ) {
                operands.let { (numbers, fonts, alignments) ->
                    numbers.forEachIndexed { index, number ->
                        Text(
                            text = "$number",
                            fontSize = with(LocalDensity.current) { fonts[index].toSp() },
                            textAlign = alignments[index],
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            DrawingArea(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(35f),
                handler = drawingAreaHandler
            )
        }

        ResultBadge(inputState = inputState) {
            LaunchedEffect(Unit) {
                onGenerateNewBoard()
            }
        }
    }
}