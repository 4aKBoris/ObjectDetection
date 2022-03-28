@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow.ui.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mpei.tensorflow.navigation.Navigation
import com.mpei.tensorflow.navigation.PhotoScreen
import com.mpei.tensorflow.navigation.Screen
import com.mpei.tensorflow.ui.bottombar.BottomBar
import com.mpei.tensorflow.ui.floatingbutton.FloatingButton
import com.mpei.tensorflow.ui.theme.Green80
import com.mpei.tensorflow.ui.theme.Purple80

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    model: String,
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

    val navController = rememberNavController()

    val backstackEntry by navController.currentBackStackEntryAsState()

    val (tabPage, setTabPage) = rememberSaveable { mutableStateOf(Screen.Photo) }

    setTabPage(
        Screen.fromRoute(
            backstackEntry?.destination?.route
        )
    )

    val (screen, setScreen) = rememberSaveable { mutableStateOf(PhotoScreen.Enter) }

    setScreen(
        PhotoScreen.fromRoute(
            backstackEntry?.destination?.route
        )
    )

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
                screen = screen
            ) { route, photoScreen, flag ->
                if (flag) navController.backQueue.clear()
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
                if (tab == Screen.Model)
                    while (navController.backQueue.size > 1)
                        navController.backQueue.removeLast()
                navController.navigate(tab.name)
            }
        }
    ) {
        Navigation(
            navController = navController,
            innerPadding = it,
            model = model,
            setModel = setModel,
            backgroundColor = backgroundColor
        )

    }
}