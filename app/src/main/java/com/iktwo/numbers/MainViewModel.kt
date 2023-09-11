package com.iktwo.numbers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier.EN
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionContext
import com.google.mlkit.vision.digitalink.RecognitionResult

enum class ModelState {
    READY, DOWNLOADING, ERROR
}

class MainViewModel : ViewModel() {
    val modelDownloadState: LiveData<ModelState>
        get() = _modelDownloadState

    val numbersToSum: LiveData<Operands>
        get() = _numbersToSum

    val lastInput: LiveData<Int?>
        get() = _lastInput

    private val _modelDownloadState = MutableLiveData(ModelState.DOWNLOADING)

    private val _numbersToSum = MutableLiveData(Operands.build(7))

    private val _lastInput = MutableLiveData<Int?>(null)

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

    init {
        remoteModelManager.download(recognitionModel, DownloadConditions.Builder().build())
            .addOnSuccessListener {
                _modelDownloadState.postValue(ModelState.READY)
            }.addOnFailureListener { e: Exception ->
                Log.e(MainViewModel::class.simpleName, "Error fetching model: $e")
                _modelDownloadState.postValue(ModelState.ERROR)
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
                        _lastInput.postValue(number)
                    } catch (e: NumberFormatException) {
                        // Not an integer
                        Log.e(MainViewModel::class.simpleName, "Couldn't parse number")
                        _lastInput.postValue(null)
                    }
                }
            }.addOnFailureListener { e: Exception ->
                Log.e(MainViewModel::class.simpleName, "Error during recognition: $e")
            }
    }
}