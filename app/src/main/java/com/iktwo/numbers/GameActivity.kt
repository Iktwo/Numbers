package com.iktwo.numbers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.digitalink.Ink
import com.iktwo.numbers.model.InputState
import com.iktwo.numbers.model.ModelState
import com.iktwo.numbers.ui.elements.DrawingArea
import com.iktwo.numbers.ui.elements.DrawingAreaHandler
import com.iktwo.numbers.ui.theme.LocalColors
import com.iktwo.numbers.ui.theme.NumbersTheme
import com.iktwo.numbers.ui.theme.Padding
import com.iktwo.numbers.ui.theme.PaddingLarge
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
class GameActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NumbersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(65f)
                                .background(
                                    when (uiState.inputState) {
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
                            uiState.operands.let { (numbers, fonts, alignments) ->
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
                            handler = object : DrawingAreaHandler {
                                override val inputState: InputState
                                    get() = uiState.inputState

                                override fun recognize(ink: Ink) {
                                    viewModel.recognize(ink)
                                }
                            }
                        )
                    }

                    //region ModelState
                    when (uiState.modelState) {
                        ModelState.DOWNLOADING -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.align(Center),
                                    verticalArrangement = Arrangement.spacedBy(
                                        PaddingLarge
                                    )
                                ) {
                                    Text(
                                        text = getString(R.string.initializing_model),
                                        // TODO: reference colors from theme
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(PaddingLarge)
                                            .align(CenterHorizontally)
                                    )
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(
                                            CenterHorizontally
                                        )
                                    )
                                }
                            }
                        }

                        ModelState.ERROR -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                            ) {
                                Text(
                                    text = getString(R.string.problem_loading_model),
                                    color = Color(0xFFa52a2a),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Padding)
                                        .align(Center)
                                )
                            }
                        }

                        ModelState.READY -> {}
                    }
                    //endregion

                    val haptic = LocalHapticFeedback.current
                    when (uiState.inputState) {
                        InputState.READY_FOR_INPUT -> {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }

                        InputState.INCORRECT -> {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }

                        InputState.CORRECT -> {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }

                    val density = LocalDensity.current
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        AnimatedVisibility(
                            visible = uiState.inputState == InputState.CORRECT,
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
                                        .align(Center)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                )
                            }

                            if (transition.currentState == transition.targetState) {
                                LaunchedEffect(Unit) {
                                    viewModel.generateNewBoard()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
