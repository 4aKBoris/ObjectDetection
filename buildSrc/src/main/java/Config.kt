import org.gradle.api.JavaVersion

object Config {
    const val targetSdk = 32
    const val minSdk = 24
    const val namespace = "com.mpei.tensorflow"
    const val version = "1.0.0"
    const val versionCode = 1
    const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val versionJVM = "11"
    val versionJava = JavaVersion.VERSION_11
}