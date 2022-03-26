buildscript {
    dependencies {
        classpath(Plugins.serialization)
        classpath(Plugins.hiltAndroid)
    }
}
plugins {
    id(Plugins.application) version Plugins.versionGradle apply false
    id(Plugins.library) version Plugins.versionGradle apply false
    id(Plugins.kotlin) version Plugins.versionKotlin apply false
}