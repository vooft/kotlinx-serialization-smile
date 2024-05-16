package io.github.vooft.kotlinsmile.decoder.shared

interface DecodingSmileSharedStorage {
    fun storeKey(key: String): Int
    fun getKey(id: Int): String
    fun storeValue(value: String): Int
    fun getValue(id: Int): String
}

class DecodingSmileSharedStorageImpl(shareKeys: Boolean, shareValues: Boolean) : DecodingSmileSharedStorage {

    private val keys: DecodingValueHolder = when (shareKeys) {
        true -> DecodingValueHolderImpl()
        false -> DisabledDecodingValueHolder(StorageType.KEYS)
    }

    private val values: DecodingValueHolder = when (shareValues) {
        true -> DecodingValueHolderImpl() // index 0 is not used currently
        false -> DisabledDecodingValueHolder(StorageType.VALUES)
    }

    override fun storeKey(key: String): Int = keys.store(key)
    override fun getKey(id: Int): String = keys.get(id)

    override fun storeValue(value: String): Int = values.store(value)
    override fun getValue(id: Int): String = values.get(id)
}
