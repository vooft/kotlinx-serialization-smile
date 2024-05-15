package io.github.vooft.kotlinsmile.decoder.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DecodingSmileSharedStorageImplTest {

    private val enabledStorage = DecodingSmileSharedStorageImpl(shareKeys = true, shareValues = true)

    @Test
    fun should_store_key() {
        val key = "key"
        val id = enabledStorage.storeKey(key)

        enabledStorage.getKey(id) shouldBe key
    }

    @Test
    fun should_store_value() {
        val value = "value"
        val id = enabledStorage.storeValue(value)

        enabledStorage.getValue(id) shouldBe value
    }

    @Test
    fun should_ignore_store_and_fail_on_retrieve_key_when_disabled() {
        val disabledStorage = DecodingSmileSharedStorageImpl(shareKeys = false, shareValues = true)

        disabledStorage.storeKey("test") shouldBe -1
        shouldThrow<StorageDisabledException> { disabledStorage.getKey(1) }
    }

    @Test
    fun should_ignore_store_and_fail_on_retrieve_value_when_disabled() {
        val disabledStorage = DecodingSmileSharedStorageImpl(shareKeys = true, shareValues = false)

        disabledStorage.storeValue("test") shouldBe -1
        shouldThrow<StorageDisabledException> { disabledStorage.getValue(1) }
    }
}
