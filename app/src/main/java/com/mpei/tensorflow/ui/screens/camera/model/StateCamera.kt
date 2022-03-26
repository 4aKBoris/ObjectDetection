package com.mpei.tensorflow.ui.screens.camera.model

import androidx.camera.core.CameraSelector

sealed class StateCamera {

    abstract val cameraSelector: CameraSelector

    data class BackCamera(val torch: Boolean): StateCamera() {
        override val cameraSelector: CameraSelector
            get() = CameraSelector.DEFAULT_BACK_CAMERA
    }

    object FrontCamera: StateCamera() {
        override val cameraSelector: CameraSelector
            get() = CameraSelector.DEFAULT_FRONT_CAMERA
    }
}
