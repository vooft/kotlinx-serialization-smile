package io.github.vooft.kotlinsmile.common.shared

interface SmileSharedStorage {
    fun storeKey(key: String): Int
    fun getKeyById(id: Int): String
    fun lookupKey(key: String): Int?
    fun storeValue(value: String): Int
    fun getValueById(id: Int): String
    fun lookupValue(value: String): Int?
}

class SmileSharedStorageImpl(shareKeys: Boolean, shareValues: Boolean) : SmileSharedStorage {
    private val keys: SharedValueStorage = when (shareKeys) {
        true -> SharedValueStorageImpl()
        false -> DisabledSharedValueStorage(StorageType.KEYS)
    }

    private val values: SharedValueStorage = when (shareValues) {
        true -> SharedValueStorageImpl()
        false -> DisabledSharedValueStorage(StorageType.VALUES)
    }

    override fun storeKey(key: String): Int = keys.store(key)
    override fun getKeyById(id: Int): String = keys.getById(id)
    override fun lookupKey(key: String): Int? = keys.lookup(key)

    override fun storeValue(value: String): Int = values.store(value)
    override fun getValueById(id: Int): String = values.getById(id)
    override fun lookupValue(value: String): Int? = values.lookup(value)
}
