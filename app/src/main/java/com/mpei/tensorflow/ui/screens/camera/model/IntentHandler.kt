package com.mpei.tensorflow.ui.screens.camera.model

interface IntentHandler<T> {
    fun obtainIntent(intent: T)
}