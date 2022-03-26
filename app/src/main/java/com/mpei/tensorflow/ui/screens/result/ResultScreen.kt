package com.mpei.tensorflow.ui.screens.result

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mpei.tensorflow.DetectionResult
import com.mpei.tensorflow.ui.theme.Green80
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ResultScreen(uri: Uri?, model: String) {

    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(key1 = uri) {
        try {
            val newBitmap =
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        uri!!
                    )
                )
                    .copy(Bitmap.Config.ARGB_8888, true)
            bitmap = runObjectDetection(context, newBitmap, model).asImageBitmap()
        } catch (e: Exception) {
            Log.d("ResultScreen", e.message!!)
        }
    }
    Crossfade(
        targetState = bitmap == null,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    ) {
        when (it) {
            true -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = Green80,
                    strokeWidth = 3.dp
                )
            }
            false -> Image(
                bitmap = bitmap!!,
                contentDescription = "Фотография с распознанными объектами",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

private fun runObjectDetection(context: Context, bitmap: Bitmap, model: String): Bitmap {

    val image = TensorImage.fromBitmap(bitmap)

    val options = ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(5)
        .setScoreThreshold(0.3f)
        .build()
    val detector = ObjectDetector.createFromFileAndOptions(
        context,
        "$model.tflite",
        options
    )

    val results = detector.detect(image)

    val resultToDisplay = results.map {
        val category = it.categories.first()
        val text = "${category.label}, ${category.score.times(100).toInt()}%"
        DetectionResult(it.boundingBox, text)
    }

    return drawDetectionResult(bitmap, resultToDisplay)
}

private const val MAX_FONT_SIZE = 96F

private fun drawDetectionResult(
    bitmap: Bitmap,
    detectionResults: List<DetectionResult>
): Bitmap {
    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)
    val pen = Paint()
    pen.textAlign = Paint.Align.LEFT

    detectionResults.forEach {
        pen.color = Color.RED
        pen.strokeWidth = 8F
        pen.style = Paint.Style.STROKE
        val box = it.boundingBox
        canvas.drawRect(box, pen)

        val tagSize = Rect(0, 0, 0, 0)

        pen.style = Paint.Style.FILL_AND_STROKE
        pen.color = Color.YELLOW
        pen.strokeWidth = 2F

        pen.textSize = MAX_FONT_SIZE
        pen.getTextBounds(it.text, 0, it.text.length, tagSize)
        val fontSize: Float = pen.textSize * box.width() / tagSize.width()

        if (fontSize < pen.textSize) pen.textSize = fontSize

        var margin = (box.width() - tagSize.width()) / 2.0F
        if (margin < 0F) margin = 0F
        canvas.drawText(
            it.text, box.left + margin,
            box.top + tagSize.height().times(1F), pen
        )
    }
    return outputBitmap
}