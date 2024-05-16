package io.github.vooft.kotlinsmile.decoder.shared

interface DecodingValueHolder {
    fun store(value: String): Int
    fun get(id: Int): String
}

class DecodingValueHolderImpl : DecodingValueHolder {
    private val valueToId = mutableMapOf<String, Int>()
    private val valuesList = mutableListOf<String>()

    override fun store(value: String): Int {
        if (valuesList.size >= MAX_STORAGE_SIZE) {
            valuesList.clear()
            valueToId.clear()
        }

        val existingIndex = valueToId[value]
        if (existingIndex != null) {
            return existingIndex
        }

        val newIndex = valuesList.size
//        if (!isJacksonValidIndex(newIndex)) {
//            val invalidString = nextInvalidString()
//            valueToId[invalidString] = valuesList.size
//            valuesList.add(invalidString)
//            return newIndex
//        }

        valueToId[value] = newIndex
        valuesList.add(value)

        return newIndex
    }

    override fun get(id: Int): String {
//        if (!isJacksonValidIndex(id)) {
//            return nextInvalidString()
//        }
        return valuesList[id]
    }
}

// logic copied from Jackson in order to be compatible https://github.com/FasterXML/jackson-dataformats-binary/issues/495
private fun isJacksonValidIndex(index: Int): Boolean {
    return (index and 0xFF) < 0xFE
}

private var invalidStringCounter = 0
private val invalidStringPrefix = "a".repeat(65)
private fun nextInvalidString() = invalidStringPrefix + invalidStringCounter++

class DisabledDecodingValueHolder(private val storageType: StorageType) : DecodingValueHolder {
    override fun store(value: String): Int = -1
    override fun get(id: Int): String = throw StorageDisabledException(storageType)
}

class StorageDisabledException(storageType: StorageType) : IllegalStateException("Shared storage for $storageType is disabled")

enum class StorageType {
    KEYS,
    VALUES
}

private const val MAX_STORAGE_SIZE = 1024
