package com.iktwo.numbers.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier.EN
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionContext
import com.google.mlkit.vision.digitalink.RecognitionResult
import com.iktwo.numbers.model.state.InputState
import com.iktwo.numbers.model.state.ModelState
import com.iktwo.numbers.model.Operands
import com.iktwo.numbers.model.state.PageEntry
import com.iktwo.numbers.model.uistate.MainMenuUIState
import com.iktwo.numbers.model.uistate.SumGameUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val amountOfOperands = 5

    private val _mainMenuUIStateFlow = MutableStateFlow(MainMenuUIState(modelState = ModelState.READY))

    val mainMenuUIStateFlow: StateFlow<MainMenuUIState> = _mainMenuUIStateFlow

    private val _sumGameUIStateFlow = MutableStateFlow(
        SumGameUIState(
            inputState = InputState.READY_FOR_INPUT,
            operands = Operands.buildRandom(amountOfOperands)
        )
    )

    val sumGameUIStateFlow: StateFlow<SumGameUIState> = _sumGameUIStateFlow.asStateFlow()

    private val regexDigits = Regex("^[0-9]*\$")

    //region Recognition
    private val recognitionModel = DigitalInkRecognitionModel.builder(EN).build()
    private val remoteModelManager = RemoteModelManager.getInstance()

    // Setting pre context as "Number:" seems to vastly improve digit recognition
    private val recognitionContext = RecognitionContext.builder()
        .setPreContext("Number:").build()

    private val recognizer = DigitalInkRecognition.getClient(
        DigitalInkRecognizerOptions.builder(recognitionModel).build()
    )
    //endregion

    private var recognitionJob: Job? = null
    private var lastRecognizedInput: Int? = null

    init {
        remoteModelManager.isModelDownloaded(recognitionModel).addOnSuccessListener { downloaded ->
            if (!downloaded) {
                _mainMenuUIStateFlow.update { state ->
                    state.copy(modelState = ModelState.DOWNLOADING)
                }

                downloadModel()
            } else {
                _mainMenuUIStateFlow.update { state ->
                    state.copy(modelState = ModelState.READY)
                }
            }
        }
    }

    private fun downloadModel() {
        remoteModelManager.download(recognitionModel, DownloadConditions.Builder().build())
            .addOnSuccessListener {
                _mainMenuUIStateFlow.update { state ->
                    state.copy(modelState = ModelState.READY)
                }
            }.addOnFailureListener { e: Exception ->
                Log.e(MainViewModel::class.simpleName, "Error fetching model: $e")

                _mainMenuUIStateFlow.update { state ->
                    state.copy(modelState = ModelState.ERROR)
                }
            }
    }

    fun recognize(ink: Ink) {
        recognizer.recognize(ink, recognitionContext)
            .addOnSuccessListener { result: RecognitionResult ->
                result.candidates.firstOrNull {
                    regexDigits.matches(it.text.trim())
                }?.text?.let {
                    try {
                        val number = it.trim().toInt()
                        lastRecognizedInput = number

                        recognitionJob?.cancel()

                        if (isLastInputCorrect()) {
                            _sumGameUIStateFlow.update { state ->
                                state.copy(inputState = InputState.CORRECT)
                            }
                        } else {
                            delayPossiblyIncorrectCheck(
                                if (lastRecognizedInput.toString().length < _sumGameUIStateFlow.value.operands.sum.toString().length)
                                    1250
                                else
                                    550
                            )
                        }
                    } catch (e: NumberFormatException) {
                        // Not an integer
                        Log.e(MainViewModel::class.simpleName, "Couldn't parse number")
                        lastRecognizedInput = null
                    }
                }
            }.addOnFailureListener { e: Exception ->
                Log.e(MainViewModel::class.simpleName, "Error during recognition: $e")
            }
    }

    private fun delayPossiblyIncorrectCheck(delayInMS: Long = 550) {
        if (lastRecognizedInput == null) {
            return
        }

        recognitionJob?.cancel()
        recognitionJob = viewModelScope.launch {
            delay(delayInMS)

            _sumGameUIStateFlow.update { state ->
                state.copy(inputState = InputState.INCORRECT)
            }
        }
    }

    private fun isLastInputCorrect(): Boolean {
        return lastRecognizedInput == sumGameUIStateFlow.value.operands.sum
    }

    fun generateNewBoard() {
        lastRecognizedInput = null
        recognitionJob?.cancel()

        _sumGameUIStateFlow.update {
            it.copy(
                operands = Operands.buildRandom(amountOfOperands),
                inputState = InputState.READY_FOR_INPUT
            )
        }
    }

    fun navigate(pageEntry: PageEntry, navController: NavController) {
        _sumGameUIStateFlow.update {
            it.copy(
                operands = Operands.buildRandom(amountOfOperands),
                inputState = InputState.READY_FOR_INPUT
            )
        }

        navController.navigate(pageEntry.name)
    }
}
