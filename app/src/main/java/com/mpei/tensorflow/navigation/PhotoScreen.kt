package com.mpei.tensorflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector

enum class PhotoScreen(val icon: ImageVector, val contentDescription: String) {
    Enter(icon = Icons.Rounded.PhotoCamera, contentDescription = "Открыть камеру"),
    Camera(icon = Icons.Rounded.Circle, contentDescription = "Сделать фото"),
    Result(icon = Icons.Rounded.Delete, contentDescription = "Очистить")
}