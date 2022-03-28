package com.mpei.tensorflow.ui.screens.camera.model

import android.annotation.SuppressLint
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class CameraViewModel @Inject constructor(): ViewModel(), IntentHandler<IntentCamera> {

    @Inject
    @Singleton
    lateinit var imageCapture: ImageCapture

    @SuppressLint("StaticFieldLeak")
    @Inject
    @Singleton
    lateinit var previewView: PreviewView

    @Inject
    @Singleton
    lateinit var preview: Preview

    @Inject
    lateinit var outputOptions: ImageCapture.OutputFileOptions

    @Inject
    lateinit var executor: Executor

    private val _state: MutableStateFlow<StateCamera> = MutableStateFlow(StateCamera.BackCamera(torch = false))

    val state: StateFlow<StateCamera> = _state

    override fun obtainIntent(intent: IntentCamera) {
        when (val currentState = _state.value) {
            is StateCamera.BackCamera -> reduce(intent, currentState)
            is StateCamera.FrontCamera -> reduce(intent, currentState)
        }
    }

    private fun reduce(intent: IntentCamera, currentState: StateCamera.BackCamera) {
        when (intent) {
            IntentCamera.ChooseFrontCamera -> _state.value = StateCamera.FrontCamera
            IntentCamera.ChangeTorch -> _state.value = currentState.copy(torch = !currentState.torch)
            else -> throw NotImplementedError(message = "Invalid $intent fo state $currentState")
        }
    }

    private fun reduce(intent: IntentCamera, currentState: StateCamera.FrontCamera) {
        when (intent) {
            IntentCamera.ChooseBackCamera -> _state.value = StateCamera.BackCamera(torch = false)
            else -> throw throw NotImplementedError(message = "Invalid $intent fo state $currentState")
        }
    }

}