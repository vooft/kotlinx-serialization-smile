package io.github.vooft.kotlinsmile.decoder.shared

interface DecodingValueHolder {
    fun store(key: String): Int
    fun get(id: Int): String
}

class DecodingValueHolderImpl : DecodingValueHolder {
    private val valueToId = mutableMapOf<String, Int>()
    private val valuesList = mutableListOf<String>()

    override fun store(key: String): Int {
        if (valuesList.size >= MAX_STORAGE_SIZE) {
            valuesList.clear()
            valueToId.clear()
        }

        val existingIndex = valueToId[key]
        if (existingIndex != null) {
            return existingIndex
        }

        val newIndex = valuesList.size

        valueToId[key] = newIndex
        valuesList.add(key)

        return newIndex
    }

    override fun get(id: Int): String = valuesList[id]
}

class DisabledDecodingValueHolder(private val storageType: StorageType) : DecodingValueHolder {
    override fun store(key: String): Int = -1
    override fun get(id: Int): String = throw StorageDisabledException(storageType)
}

class StorageDisabledException(storageType: StorageType) : IllegalStateException("Shared storage for $storageType is disabled")

enum class StorageType {
    KEYS,
    VALUES
}

private const val MAX_STORAGE_SIZE = 1024
