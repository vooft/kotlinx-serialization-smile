package io.github.vooft.kotlinsmile.encoder.shared

interface EncodingSmileSharedStorage {
    fun storeKey(key: String)
    fun lookupKey(key: String): Int?

    fun storeValue(value: String)
    fun lookupValue(value: String): Int?
}

class EncodingSmileSharedStorageImpl(shareKeys: Boolean, shareValues: Boolean) : EncodingSmileSharedStorage {
    private val keyStorage = when (shareKeys) {
        true -> EncodingValueHolderImpl()
        false -> DisabledEncodingValueHolder()
    }

    private val valueStorage = when (shareValues) {
        true -> EncodingValueHolderImpl()
        false -> DisabledEncodingValueHolder()
    }

    override fun storeKey(key: String) = keyStorage.store(key)
    override fun lookupKey(key: String): Int? = keyStorage.lookup(key)

    override fun storeValue(value: String) = valueStorage.store(value)
    override fun lookupValue(value: String): Int? = valueStorage.lookup(value)
}
