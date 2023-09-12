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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.iktwo.numbers.model.InputState
import com.iktwo.numbers.model.ModelState
import com.iktwo.numbers.ui.elements.DrawingArea
import com.iktwo.numbers.ui.theme.NumbersTheme
import com.iktwo.numbers.ui.theme.Orange
import com.iktwo.numbers.ui.theme.Padding
import com.iktwo.numbers.ui.theme.PaddingLarge

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
                    val modelState =
                        viewModel.modelDownloadState.observeAsState(ModelState.DOWNLOADING)

                    val inputState = viewModel.inputState.observeAsState(InputState.READY_FOR_INPUT)

                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(65f)
                                .background(if (inputState.value == InputState.INCORRECT) Orange else Color.Gray)
                                .padding(Padding)
                        ) {
                            viewModel.numbersToSum.value?.let { (numbers, fonts, aligments) ->
                                numbers.forEachIndexed { index, number ->
                                    Text(
                                        text = "$number",
                                        fontSize = with(LocalDensity.current) { fonts[index].toSp() },
                                        textAlign = aligments[index],
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        DrawingArea(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(35f)
                        ) {
                            viewModel.recognize(it)
                        }
                    }

                    //region ModelState
                    when (modelState.value) {
                        ModelState.DOWNLOADING -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xCC000000))
                            ) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalArrangement = Arrangement.spacedBy(
                                        PaddingLarge
                                    )
                                ) {
                                    // TODO: move this to resources and translate
                                    Text(
                                        text = "Initializing model",
                                        // TODO: reference colors from theme
                                        color = Color.White,
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
                                    // TODO: move this to resources and translate
                                    text = "There was a problem loading the model. Check your internet connectivity and try again.",
                                    color = Color(0xFFa52a2a),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Padding)
                                        .align(Alignment.Center)
                                )
                            }
                        }

                        ModelState.READY -> {}
                    }
                    //endregion
                }
            }
        }
    }
}
