package io.github.vooft.kotlinsmile.common.shared

interface SharedValueStorage {
    fun store(value: String): Int
    fun lookup(value: String): Int?
    fun getById(id: Int): String
}

class SharedValueStorageImpl : SharedValueStorage {
    private val valueToId = HashMap<String, Int>(256)
    private val valuesList = ArrayList<String>(256)

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
        if (!isJacksonValidIndex(newIndex)) {
            valuesList.add(EMPTY_STRING) // put an empty string to avoid this index
        } else {
            valueToId[value] = newIndex
            valuesList.add(value)
        }

        return newIndex
    }

    override fun lookup(value: String): Int? = valueToId[value]

    override fun getById(id: Int): String {
        return valuesList[id]
    }
}

// logic copied from Jackson in order to be compatible https://github.com/FasterXML/jackson-dataformats-binary/issues/495
private fun isJacksonValidIndex(index: Int): Boolean {
    return (index and 0xFF) < 0xFE
}

class DisabledSharedValueStorage(private val storageType: StorageType) : SharedValueStorage {
    override fun store(value: String): Int = -1
    override fun lookup(value: String): Int? = null
    override fun getById(id: Int): String = throw StorageDisabledException(storageType)
}

class StorageDisabledException(storageType: StorageType) : IllegalStateException("Shared storage for $storageType is disabled")

enum class StorageType {
    KEYS,
    VALUES
}

private const val EMPTY_STRING = ""
private const val MAX_STORAGE_SIZE = 1024
