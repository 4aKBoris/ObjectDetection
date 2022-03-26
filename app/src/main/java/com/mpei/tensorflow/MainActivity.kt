@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mpei.tensorflow.navigation.Navigation
import com.mpei.tensorflow.navigation.PhotoScreen
import com.mpei.tensorflow.navigation.Screen
import com.mpei.tensorflow.ui.bottombar.BottomBar
import com.mpei.tensorflow.ui.floatingbutton.FloatingButton
import com.mpei.tensorflow.ui.screens.camera.model.CameraViewModel
import com.mpei.tensorflow.ui.theme.Green80
import com.mpei.tensorflow.ui.theme.Purple80
import com.mpei.tensorflow.ui.theme.TensorFlowTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "models")

class MainActivity : ComponentActivity() {

    private val keyModel = stringPreferencesKey("model")

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelFlow: Flow<String> = this.dataStore.data
            .map { preferences ->
                preferences[keyModel] ?: "Model1"
            }

        val cameraViewModel: CameraViewModel by viewModels()

        val imageCapture: ImageCapture = ImageCapture.Builder().build()

        setContent {
            TensorFlowTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val model by modelFlow.collectAsState(initial = "Model1")

                    val scope = rememberCoroutineScope()

                    val context = LocalContext.current

                    val setModel = fun(model: String) {
                        scope.launch(Dispatchers.IO) {
                            context.dataStore.edit { settings ->
                                settings[keyModel] = model
                            }
                        }
                    }

                    MainView(
                        model = model,
                        setModel = setModel,
                        cameraViewModel = cameraViewModel,
                        imageCapture = imageCapture
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    model: String,
    cameraViewModel: CameraViewModel,
    imageCapture: ImageCapture,
    setModel: (String) -> Unit
) {

    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    LaunchedEffect(key1 = Unit) {
        cameraPermissionState.launchMultiplePermissionRequest()
    }

    val cameraState by cameraViewModel.state.collectAsState()

    val navController = rememberNavController()

    var tabPage by rememberSaveable { mutableStateOf(Screen.Photo) }

    val (screen, setScreen) = rememberSaveable { mutableStateOf(PhotoScreen.Enter) }

    val backgroundColor by animateColorAsState(
        targetValue = if (tabPage == Screen.Model) Green80 else Purple80,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Scaffold(
        floatingActionButton = {
            FloatingButton(
                tabPage = tabPage,
                screen = screen,
                state = cameraState,
                setState = cameraViewModel::obtainIntent,
                imageCapture = imageCapture
            ) { route, photoScreen ->
                navController.navigate(route)
                setScreen(photoScreen)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomBar(
                tabPage = tabPage,
                backgroundColor = backgroundColor
            ) { tab ->
                navController.navigate(tab.name)
                tabPage = tab
                if (tab == Screen.Model) setScreen(PhotoScreen.Enter)
            }
        }
    ) {
        Navigation(
            navController = navController,
            innerPadding = it,
            model = model,
            setModel = setModel,
            backgroundColor = backgroundColor,
            imageCapture = imageCapture
        )

    }
}


