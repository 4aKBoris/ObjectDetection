package com.mpei.tensorflow.di

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CameraModule {

    private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private const val type = "image/jpeg"

    private const val path = "Pictures/CameraX-Image"

    @Provides
    @Singleton
    fun getExecutor(@ApplicationContext context: Context): Executor = ContextCompat.getMainExecutor(context)

    @Provides
    @Singleton
    fun getImageCapture(): ImageCapture = ImageCapture.Builder().build()

    @Provides
    fun getTime(): Long = System.currentTimeMillis()

    @Provides
    fun getName(time: Long): String {
        return SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(time)
    }

    @Provides
    fun getContentValues(name: String): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, type)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, path)
            }
        }
    }

    @Provides
    fun getOutputOptions(
        @ApplicationContext context: Context,
        contentValues: ContentValues
    ): ImageCapture.OutputFileOptions {
        return ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
    }

    @Provides
     @Singleton
     fun getPreviewView(@ApplicationContext context: Context): PreviewView {
         return PreviewView(context).apply {
             this.scaleType = PreviewView.ScaleType.FILL_CENTER
             layoutParams = ViewGroup.LayoutParams(
                 ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
             )
         }
     }

     @Provides
     @Singleton
     fun getPreviewUseCase(previewView: PreviewView): Preview {
         return Preview.Builder().build().also {
             it.setSurfaceProvider(previewView.surfaceProvider)
         }
     }
}