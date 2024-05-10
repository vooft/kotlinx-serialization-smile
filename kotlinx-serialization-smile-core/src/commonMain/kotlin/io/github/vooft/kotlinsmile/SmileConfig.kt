package io.github.vooft.kotlinsmile

data class SmileConfig(val shareStringValue: Boolean, val sharePropertyName: Boolean) {
    companion object {
        val DEFAULT = SmileConfig(shareStringValue = false, sharePropertyName = false)
    }
}
