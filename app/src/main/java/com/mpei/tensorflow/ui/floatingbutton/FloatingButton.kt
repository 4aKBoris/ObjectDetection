@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow.ui.floatingbutton

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.FlashlightOff
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpei.tensorflow.di.LocalImageCapture
import com.mpei.tensorflow.navigation.PhotoScreen
import com.mpei.tensorflow.navigation.Screen
import com.mpei.tensorflow.ui.screens.camera.model.CameraViewModel
import com.mpei.tensorflow.ui.screens.camera.model.IntentCamera
import com.mpei.tensorflow.ui.screens.camera.model.StateCamera
import com.mpei.tensorflow.ui.screens.camera.takePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FloatingButton(
    tabPage: Screen,
    screen: PhotoScreen,
    viewModel: CameraViewModel = hiltViewModel(),
    navigate: (String, PhotoScreen, Boolean) -> Unit
) {

    val state by viewModel.state.collectAsState()

    val length by animateDpAsState(
        targetValue = if (screen == PhotoScreen.Camera) 128.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )

    val padding by animateDpAsState(
        targetValue = if (screen == PhotoScreen.Camera) 0.dp else 9.dp,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )

    val width = LocalConfiguration.current.screenWidthDp / 2 * 3 - 129

    AnimatedVisibility(
        visible = tabPage == Screen.Photo,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        enter = scaleIn(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            initialScale = 0f
        ) + fadeIn(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            initialAlpha = 0f
        ),
        exit = scaleOut(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            targetScale = 0f
        ) + fadeOut(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            targetAlpha = 0f
        )
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .height(40.dp)
                    .width(length)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .align(Alignment.Center)
            )

            ButtonCamera(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, top = 9.dp, bottom = 9.dp),
                width = width,
                visible = screen == PhotoScreen.Camera,
                state = state,
                setState = viewModel::obtainIntent
            )

            ButtonTorch(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, bottom = 9.dp, top = 9.dp),
                width = width,
                visible = screen == PhotoScreen.Camera,
                state = state,
                setState = viewModel::obtainIntent
            )

            ButtonCenter(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = padding),
                screen = screen,
                navigate = navigate
            )

        }
    }
}

@Composable
private fun ButtonCamera(
    modifier: Modifier,
    width: Int,
    visible: Boolean,
    state: StateCamera,
    setState: (IntentCamera) -> Unit
) {
    AnimateButton(
        visible = visible, modifier = modifier, width = -width
    ) {

        val cameraRotate by animateFloatAsState(
            targetValue = if (state is StateCamera.BackCamera) 360f else 0f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        FloatingButtonIcon(
            modifier = Modifier.rotate(cameraRotate),
            imageVector = Icons.Rounded.Cameraswitch,
            contentDescription = "Переключить камеру",
            onClick = {
                setState(
                    if (state is StateCamera.BackCamera) IntentCamera.ChooseFrontCamera
                    else IntentCamera.ChooseBackCamera
                )
            }
        )
    }
}

@Composable
private fun ButtonTorch(
    modifier: Modifier,
    width: Int,
    visible: Boolean,
    state: StateCamera,
    setState: (IntentCamera) -> Unit
) {
    AnimateButton(
        visible = visible, modifier = modifier, width = width
    ) {
        Crossfade(
            targetState = if (state is StateCamera.BackCamera && state.torch) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff,
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        ) {
            FloatingButtonIcon(
                imageVector = it,
                contentDescription = "Включить/Выключить фонарик",
                onClick = { if (state is StateCamera.BackCamera) setState(IntentCamera.ChangeTorch) }
            )
        }
    }
}

@Composable
private fun ButtonCenter(
    modifier: Modifier,
    screen: PhotoScreen,
    viewModel: CameraViewModel = hiltViewModel(),
    navigate: (String, PhotoScreen, Boolean) -> Unit
) {

    val size by animateDpAsState(
        targetValue = if (screen == PhotoScreen.Camera) 72.dp else 54.dp,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )

    val scope = rememberCoroutineScope()

    val imageCapture = LocalImageCapture.current

    Crossfade(
        targetState = screen.icon,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
        modifier = modifier
    ) {

        FloatingButtonIcon(
            imageVector = it,
            contentDescription = screen.contentDescription,
            size = size
        ) {
            when (screen) {
                PhotoScreen.Enter -> navigate(PhotoScreen.Camera.name, PhotoScreen.Camera, false)
                PhotoScreen.Camera -> {
                    scope.launch(Dispatchers.Default) {
                        takePhoto(imageCapture = imageCapture,
                            executor = viewModel.executor,
                            outputOptions = viewModel.outputOptions,
                            onSuccess = {
                                scope.launch(Dispatchers.Main) {
                                    navigate("${PhotoScreen.Result.name}/$it", PhotoScreen.Result, false)
                                }
                            },
                            onError = {
                                scope.launch(Dispatchers.Main) {
                                    navigate(PhotoScreen.Enter.name, PhotoScreen.Enter, true)
                                }
                            })

                    }
                }
                PhotoScreen.Result -> navigate(PhotoScreen.Enter.name, PhotoScreen.Enter, true)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimateButton(
    visible: Boolean,
    modifier: Modifier,
    width: Int,
    body: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInHorizontally(
            animationSpec = spring(
                stiffness = Spring.StiffnessVeryLow,
                dampingRatio = Spring.DampingRatioMediumBouncy
            ),
            initialOffsetX = { width }
        ) + scaleIn(
            animationSpec = spring(
                stiffness = Spring.StiffnessVeryLow,
                dampingRatio = Spring.DampingRatioMediumBouncy
            ),
            initialScale = 0f
        ),
        exit = slideOutHorizontally(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            targetOffsetX = { width }
        ) + scaleOut(
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
            targetScale = 0f
        )
    ) {
        body()
    }

}

@Composable
private fun FloatingButtonIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    size: Dp = 54.dp,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape), onClick = onClick
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp)
        )
    }
}