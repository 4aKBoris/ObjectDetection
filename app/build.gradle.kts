plugins {
    id(Plugins.application)
    id(Plugins.kotlin)
    id(Plugins.kapt)
    id(Plugins.hilt)
}

apply(plugin = "kotlinx-serialization")

android {
    namespace = Config.namespace
    compileSdk = Config.targetSdk

    defaultConfig {
        applicationId = Config.namespace
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.version

        testInstrumentationRunner = Config.testRunner
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = Config.versionJava
        targetCompatibility = Config.versionJava
    }
    kotlinOptions {
        jvmTarget = Config.versionJVM
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.Compose.version
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.icons)
    implementation(Dependencies.Compose.toolingPreview)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.activity)
    implementation(Dependencies.Compose.navigation)

    androidTestImplementation(Dependencies.Compose.testJunit4)

    debugImplementation(Dependencies.Compose.tooling)
    debugImplementation(Dependencies.Compose.testManifest)

    implementation(Dependencies.DataStore.datastore)
    implementation(Dependencies.DataStore.datastorePreferences)

    implementation(Dependencies.Camera.camera)
    implementation(Dependencies.Camera.cameraLifecycle)
    implementation(Dependencies.Camera.cameraView)

    implementation(Dependencies.Hilt.hilt)
    implementation(Dependencies.Hilt.hiltCompose)
    kapt(Dependencies.Hilt.hiltCompiler)

    implementation(Dependencies.Androidx.core)
    implementation(Dependencies.Androidx.lifecycle)

    implementation(Dependencies.Kotlinx.coroutines)
    implementation(Dependencies.Kotlinx.serialization)

    implementation(Dependencies.Tensorflow.tensorflow)

    implementation(Dependencies.Accompanist.accompanist)

    testImplementation(Dependencies.Test.junitTest)
    androidTestImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.espresso)
}