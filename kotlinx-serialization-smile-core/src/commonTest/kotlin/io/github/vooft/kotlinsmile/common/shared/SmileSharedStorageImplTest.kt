package io.github.vooft.kotlinsmile.common.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SmileSharedStorageImplTest {

    private val enabledStorage = SmileSharedStorageImpl(shareKeys = true, shareValues = true)

    @Test
    fun should_store_key() {
        val key = "key"
        val id = enabledStorage.storeKey(key)

        enabledStorage.getKeyById(id) shouldBe key
        enabledStorage.lookupKey(key) shouldBe id
    }

    @Test
    fun should_store_value() {
        val value = "value"
        val id = enabledStorage.storeValue(value)

        enabledStorage.getValueById(id) shouldBe value
        enabledStorage.lookupValue(value) shouldBe id
    }

    @Test
    fun should_ignore_store_and_fail_on_retrieve_key_when_disabled() {
        val disabledStorage = SmileSharedStorageImpl(shareKeys = false, shareValues = true)

        disabledStorage.storeKey("test") shouldBe -1
        disabledStorage.lookupKey("test") shouldBe null
        shouldThrow<StorageDisabledException> { disabledStorage.getKeyById(1) }
    }

    @Test
    fun should_ignore_store_and_fail_on_retrieve_value_when_disabled() {
        val disabledStorage = SmileSharedStorageImpl(shareKeys = true, shareValues = false)

        disabledStorage.storeValue("test") shouldBe -1
        disabledStorage.lookupValue("test") shouldBe null
        shouldThrow<StorageDisabledException> { disabledStorage.getValueById(1) }
    }
}

