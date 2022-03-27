@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mpei.tensorflow.ui.screens.main.MainView
import com.mpei.tensorflow.ui.theme.TensorFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val Context.dataStore by preferencesDataStore(name = "models")

    private val keyModel = stringPreferencesKey("model")

    private val imageCapture = ImageCapture.Builder().build()

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
                        MainView(model = model, setModel = ::setModel)
                    }
                }
            }
        }
    }
}

val LocalImageCapture = compositionLocalOf { ImageCapture.Builder().build() }

private const val StartModel = "Model1"




