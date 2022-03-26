buildscript {
    dependencies {
        classpath(Plugins.serialization)
    }
}
plugins {
    id(Plugins.application) version Plugins.versionGradle apply false
    id(Plugins.library) version Plugins.versionGradle apply false
    id(Plugins.kotlin) version Plugins.versionKotlin apply false
}