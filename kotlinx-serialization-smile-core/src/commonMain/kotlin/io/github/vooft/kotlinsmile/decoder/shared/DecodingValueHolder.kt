package io.github.vooft.kotlinsmile.decoder.shared

interface DecodingValueHolder {
    fun store(key: String): Int
    fun get(id: Int): String
}

class DecodingValueHolderImpl(private val indexOffset: Int = 0) : DecodingValueHolder {
    private val valueToId = mutableMapOf<String, Int>().apply { repeat(indexOffset) { put("INVALID_STRING", it) } }
    private val valuesList = mutableListOf<String>().apply { repeat(indexOffset) { add("INVALID_STRING") } }

    override fun store(key: String): Int {
        if (valuesList.size >= MAX_STORAGE_SIZE) {
            valuesList.apply {
                clear()
                repeat(indexOffset) { add("INVALID_STRING") }
            }
            valueToId.apply {
                clear()
                repeat(indexOffset) { put("INVALID_STRING", it) }
            }
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
