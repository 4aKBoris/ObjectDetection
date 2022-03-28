package com.mpei.tensorflow.ui.screens.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.mpei.tensorflow.di.LocalImageCapture
import com.mpei.tensorflow.navigation.BitmapData
import com.mpei.tensorflow.ui.screens.camera.model.CameraViewModel
import com.mpei.tensorflow.ui.screens.camera.model.StateCamera
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executor


private inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    val imageCapture = LocalImageCapture.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsState()

    AndroidView(modifier = Modifier
        .fillMaxSize(), factory = {
        viewModel.previewView
    })

    LaunchedEffect(key1 = Unit) {

        val torch = snapshotFlow { if (state is StateCamera.BackCamera) (state as StateCamera.BackCamera).torch else false }

        val cameraSelector = snapshotFlow { state.cameraSelector }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            try {
                cameraProvider.unbindAll()

                var camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    viewModel.preview,
                    imageCapture
                )

                scope.launch {
                    cameraSelector.collectLatest {

                        cameraProvider.unbindAll()

                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            it,
                            viewModel.preview,
                            imageCapture
                        )
                    }
                }

                val cameraControl = camera.cameraControl

                val cameraInfo = camera.cameraInfo

                viewModel.previewView.afterMeasured {
                    viewModel.previewView.setOnTouchListener { _, event ->
                        return@setOnTouchListener when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                val factory: MeteringPointFactory =
                                    SurfaceOrientedMeteringPointFactory(
                                        viewModel.previewView.width.toFloat(), viewModel.previewView.height.toFloat()
                                    )
                                val autoFocusPoint = factory.createPoint(event.x, event.y)
                                cameraControl.startFocusAndMetering(
                                    FocusMeteringAction.Builder(
                                        autoFocusPoint,
                                        FocusMeteringAction.FLAG_AF
                                    ).apply {
                                        disableAutoCancel()
                                    }.build()
                                )
                                true
                            }
                            else -> false
                        }
                    }
                }

                scope.launch {
                    torch.collectLatest {
                        cameraControl.enableTorch(it)
                    }
                }

                val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        val zoom = cameraInfo.zoomState.value?.zoomRatio ?: 1f
                        val delta = detector.scaleFactor
                        cameraControl.setZoomRatio(zoom * delta)
                        return true
                    }
                }

                val scaleGestureDetector = ScaleGestureDetector(context, listener)

                viewModel.previewView.setOnTouchListener { _, event ->
                    scaleGestureDetector.onTouchEvent(event)
                    return@setOnTouchListener true
                }
            } catch (exc: Exception) {
                Log.e(ContentValues.TAG, "Use case binding failed", exc)
            }

        }, viewModel.executor)
    }
}

fun takePhoto(
    imageCapture: ImageCapture,
    outputOptions: ImageCapture.OutputFileOptions,
    executor: Executor,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
) {
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                onError()
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                if (output.savedUri == null) onError()
                else {
                    val uriString = output.savedUri.toString()
                    val byteArray = uriString.toByteArray(charset = Charsets.UTF_8)
                    val json = Json.encodeToString(BitmapData(byteArray))
                    onSuccess(json)
                }
            }
        }
    )
}
