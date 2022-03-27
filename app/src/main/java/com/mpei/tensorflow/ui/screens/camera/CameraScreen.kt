package com.mpei.tensorflow.ui.screens.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.mpei.tensorflow.LocalImageCapture
import com.mpei.tensorflow.navigation.BitmapData
import com.mpei.tensorflow.ui.screens.camera.model.StateCamera
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

private val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

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
private fun Context.getPreviewView(scaleType: PreviewView.ScaleType): PreviewView {
    return PreviewView(this).apply {
        this.scaleType = scaleType
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}

private fun PreviewView.getPreviewUseCase(): Preview {
    return Preview.Builder().build().also {
        it.setSurfaceProvider(this.surfaceProvider)
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraScreen(
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    state: StateCamera
) {
    val imageCapture = LocalImageCapture.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var preview by remember { mutableStateOf(context.getPreviewView(scaleType)) }

    AndroidView(modifier = Modifier.fillMaxSize(), factory = {
        preview = it.getPreviewView(scaleType)
        preview
    })

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({

        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        try {
            cameraProvider.unbindAll()

            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                state.cameraSelector,
                preview.getPreviewUseCase(),
                imageCapture
            )

            if (state is StateCamera.BackCamera) camera.cameraControl.enableTorch(state.torch)

            preview.afterMeasured {
                preview.setOnTouchListener { _, event ->
                    return@setOnTouchListener when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                                preview.width.toFloat(), preview.height.toFloat()
                            )
                            val autoFocusPoint = factory.createPoint(event.x, event.y)
                            try {
                                camera.cameraControl.startFocusAndMetering(
                                    FocusMeteringAction.Builder(
                                        autoFocusPoint,
                                        FocusMeteringAction.FLAG_AF
                                    ).apply {
                                        disableAutoCancel()
                                    }.build()
                                )
                            } catch (e: CameraInfoUnavailableException) {
                                Log.d("ERROR", "cannot access camera", e)
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
        } catch (exc: Exception) {
            Log.e(ContentValues.TAG, "Use case binding failed", exc)
        }

    }, context.executor)
}

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
) {
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()


    imageCapture.takePicture(
        outputOptions,
        context.executor,
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
