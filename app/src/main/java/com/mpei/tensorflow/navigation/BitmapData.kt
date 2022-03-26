package com.mpei.tensorflow.navigation

@kotlinx.serialization.Serializable
data class BitmapData(val uri: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BitmapData

        if (!uri.contentEquals(other.uri)) return false

        return true
    }

    override fun hashCode(): Int {
        return uri.contentHashCode()
    }
}