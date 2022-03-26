package com.mpei.tensorflow.ui.screens.camera.model

sealed class IntentCamera {
    object ChooseBackCamera: IntentCamera()
    object ChooseFrontCamera: IntentCamera()
    object ChangeTorch: IntentCamera()
}
