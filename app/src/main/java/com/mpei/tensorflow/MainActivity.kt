@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.mpei.tensorflow.di.LocalImageCapture
import com.mpei.tensorflow.ui.screens.main.MainView
import com.mpei.tensorflow.ui.theme.TensorFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var keyModel: Preferences.Key<String>

    @Inject
    lateinit var imageCapture: ImageCapture

    @Inject
    lateinit var previewView: PreviewView

    @Inject
    lateinit var preview: Preview

    @Inject
    lateinit var outputOptions: ImageCapture.OutputFileOptions

    @Inject
    lateinit var executor: Executor

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelFlow: Flow<String> = this.dataStore.data
            .map { preferences ->
                preferences[keyModel] ?: StartModel
            }

        setContent {

            TensorFlowTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val scope = rememberCoroutineScope()

                    val context = LocalContext.current

                    val model by modelFlow.collectAsState(initial = StartModel)

                    fun setModel(model: String) {
                        scope.launch(Dispatchers.IO) {
                            context.dataStore.edit { settings ->
                                settings[keyModel] = model
                            }
                        }
                    }

                    CompositionLocalProvider(
                        LocalImageCapture providesDefault imageCapture
                    ) {
                        MainView(
                            model = model,
                            setModel = ::setModel,
                            preview = preview,
                            previewView = previewView,
                            executor = executor,
                            outputOptions = outputOptions
                        )
                    }
                }
            }
        }
    }

    companion object {

        private const val StartModel = "Model1"

        private val Context.dataStore by preferencesDataStore(name = "models")
    }
}




