package com.mpei.tensorflow.navigation

enum class Screen {
    Photo, Model;

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route?.substringBefore("/")) {
                Model.name -> Model
                else -> Photo
            }
        }
    }
}