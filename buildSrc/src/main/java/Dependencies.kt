object Dependencies {

    object Compose {
        const val version = "1.1.1"
        private const val versionMaterial3 = "1.0.0-alpha08"
        private const val versionActivity = "1.4.0"
        private const val versionNavigation = "2.5.0-alpha03"

        const val ui = "androidx.compose.ui:ui:$version"
        const val icons = "androidx.compose.material:material-icons-extended:$version"
        const val toolingPreview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val material = "androidx.compose.material:material:$version"
        const val material3 = "androidx.compose.material3:material3:$versionMaterial3"
        const val activity = "androidx.activity:activity-compose:$versionActivity"
        const val navigation = "androidx.navigation:navigation-compose:$versionNavigation"

        const val testJunit4 = "androidx.compose.ui:ui-test-junit4:$version"

        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val testManifest = "androidx.compose.ui:ui-test-manifest:$version"
    }

    object DataStore {
        private const val version = "1.0.0"

        const val datastore = "androidx.datastore:datastore:$version"
        const val datastorePreferences = "androidx.datastore:datastore-preferences:$version"
    }

    object Camera {
        private const val version = "1.1.0-beta02"

        const val camera = "androidx.camera:camera-camera2:$version"
        const val cameraLifecycle = "androidx.camera:camera-lifecycle:$version"
        const val cameraView = "androidx.camera:camera-view:$version"
    }

    object Androidx {
        private const val versionCore = "1.7.0"
        private const val versionLifecycle = "2.4.1"

        const val core = "androidx.core:core-ktx:$versionCore"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:$versionLifecycle"
    }

    object Kotlinx {
        private const val versionCoroutines = "1.6.0"
        private const val versionSerialization = "1.3.2"

        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$versionCoroutines"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:$versionSerialization"
    }

    object Tensorflow {
        private const val version = "0.3.1"

        const val tensorflow = "org.tensorflow:tensorflow-lite-task-vision:$version"
    }

    object Accompanist {
        private const val version = "0.19.0"

        const val accompanist = "com.google.accompanist:accompanist-permissions:$version"
    }

    object Test {
        private const val versionJunitTest = "4.13.2"
        private const val versionJunit = "1.1.3"
        private const val versionEspresso = "3.4.0"

        const val junitTest = "junit:junit:$versionJunitTest"
        const val junit = "androidx.test.ext:junit:$versionJunit"
        const val espresso = "androidx.test.espresso:espresso-core:$versionEspresso"
    }

    object Hilt {
        private const val versionHilt = "2.40.5"
        private const val versionHiltCompose = "1.0.0"

        const val hilt = "com.google.dagger:hilt-android:$versionHilt"
        const val hiltCompose = "androidx.hilt:hilt-navigation-compose:$versionHiltCompose"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:$versionHilt"
    }
}