package com.mpei.tensorflow.di

import androidx.compose.runtime.compositionLocalOf

val LocalImageCapture = compositionLocalOf { CameraModule.getImageCapture() }


