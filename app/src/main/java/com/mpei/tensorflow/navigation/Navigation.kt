package com.mpei.tensorflow.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.mpei.tensorflow.ui.screens.camera.CameraScreen
import com.mpei.tensorflow.ui.screens.camera.model.CameraViewModel
import com.mpei.tensorflow.ui.screens.enter.EnterScreen
import com.mpei.tensorflow.ui.screens.model.ModelScreen
import com.mpei.tensorflow.ui.screens.result.ResultScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Navigation(
    navController: NavHostController,
    innerPadding: PaddingValues,
    backgroundColor: Color,
    imageCapture: ImageCapture,
    model: String,
    setModel: (String) -> Unit,
    cameraViewModel: CameraViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Photo.name,
        modifier = Modifier.padding(paddingValues = innerPadding),
    ) {
        photoScreens(
            backgroundColor = backgroundColor,
            imageCapture = imageCapture,
            cameraViewModel = cameraViewModel,
            model = model
        )
        composable(Screen.Model.name) {
            ModelScreen(model = model, setModel = setModel, backgroundColor = backgroundColor)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.P)
private fun NavGraphBuilder.photoScreens(
    backgroundColor: Color,
    imageCapture: ImageCapture,
    cameraViewModel: CameraViewModel,
    model: String
) {
    navigation(startDestination = PhotoScreen.Enter.name, route = Screen.Photo.name) {
        composable(PhotoScreen.Enter.name) {
            EnterScreen(backgroundColor = backgroundColor)
        }
        composable(PhotoScreen.Camera.name) {

            val state by cameraViewModel.state.collectAsState()

            CameraScreen(imageCapture = imageCapture, state = state)

        }
        composable(route = "${PhotoScreen.Result.name}/{$uriKey}",
            arguments = listOf(
                navArgument(uriKey) {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString(uriKey)
            requireNotNull(uriString) { "phoneNumber parameter wasn't found. Please make sure it's set!" }
            val byteArray = Json.decodeFromString<BitmapData>(uriString).uri
            val uri = Uri.parse(byteArray.decodeToString())
            ResultScreen(uri = uri, model = model)
        }
    }
}

private const val uriKey = "UriString"