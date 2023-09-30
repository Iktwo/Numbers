package com.iktwo.numbers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.digitalink.Ink
import com.iktwo.numbers.model.state.InputState
import com.iktwo.numbers.model.state.ModelState
import com.iktwo.numbers.model.state.PageEntry
import com.iktwo.numbers.ui.elements.DrawingAreaHandler
import com.iktwo.numbers.ui.pages.MainMenu
import com.iktwo.numbers.ui.pages.PageMultiply
import com.iktwo.numbers.ui.pages.PageSums
import com.iktwo.numbers.ui.pages.PageTapOrdered
import com.iktwo.numbers.ui.theme.LocalColors
import com.iktwo.numbers.ui.theme.NumbersTheme
import com.iktwo.numbers.ui.theme.Padding
import com.iktwo.numbers.ui.theme.PaddingLarge
import com.iktwo.numbers.viewmodel.MainViewModel

class GameActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NumbersTheme {
                val navController = rememberNavController()

                val sumGameUIState by viewModel.sumGameUIStateFlow.collectAsState()
                val mainMenuUIStateFlow by viewModel.mainMenuUIStateFlow.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = PageEntry.MAIN_MENU.name
                ) {
                    composable(PageEntry.MAIN_MENU.name) {
                        MainMenu(
                            backgroundColor = MaterialTheme.colorScheme.background,
                            entries = PageEntry.values().filter { it != PageEntry.MAIN_MENU }
                        ) {
                            viewModel.navigate(it, navController)
                        }
                    }

                    composable(PageEntry.SUMS.name) {
                        PageSums(
                            inputState = sumGameUIState.inputState,
                            operands = sumGameUIState.operands,
                            drawingAreaHandler = object : DrawingAreaHandler {
                                override val inputState: InputState
                                    get() = sumGameUIState.inputState

                                override fun recognize(ink: Ink) {
                                    viewModel.recognize(ink)
                                }
                            },
                            onGenerateNewBoard = {
                                viewModel.generateNewBoard()
                            }
                        )
                    }

                    composable(PageEntry.MULTIPLY.name) {
                        PageMultiply()
                    }

                    composable(PageEntry.TAP_SMALLEST.name) {
                        PageTapOrdered(increasing = true)
                    }

                    composable(PageEntry.TAP_LARGEST.name) {
                        PageTapOrdered(increasing = false)
                    }
                }

                //region ModelState
                when (mainMenuUIStateFlow.modelState) {
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
                                color = LocalColors.current.inputFailureColor,
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
                when (sumGameUIState.inputState) {
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
            }
        }
    }
}
