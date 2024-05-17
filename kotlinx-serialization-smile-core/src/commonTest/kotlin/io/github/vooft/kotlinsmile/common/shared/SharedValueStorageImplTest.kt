package io.github.vooft.kotlinsmile.common.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import kotlin.random.Random
import kotlin.test.Test

class SharedValueStorageImplTest {
    private val storage = SharedValueStorageImpl()

    @Test
    fun should_store_and_retrieve_value() {
        val value = Random.nextLong().toString()

        val id = storage.store(value)

        id shouldBe 0
        storage.getById(id) shouldBe value
    }

    @Test
    fun should_store_and_retrieve_multiple_values() {
        val values = 10
        for (i in 0..values) {
            val value = Random.nextLong().toString()

            val id = storage.store(value)

            id shouldBe i
            storage.getById(id) shouldBe value
        }
    }

    @Test
    fun should_store_duplicate_under_same_id() {
        val value = Random.nextLong().toString()

        val id1 = storage.store(value)
        val id2 = storage.store(value)

        id1 shouldBe 0
        id2 shouldBe 0
        storage.getById(id1) shouldBe value
    }

    @Test
    fun should_lookup_value() {
        val value = Random.nextLong().toString()

        val id = storage.store(value)

        storage.lookup(value) shouldBe id
    }

    @Test
    fun should_return_null_on_lookup_for_non_existing_value() {
        val value = Random.nextLong().toString()
        storage.lookup(value) shouldBe null
    }

    @Test
    fun should_fake_insert_on_254_255() {
        var counter = 0
        repeat(254) { storage.store((counter++).toString()) }

        val value254 = "254"
        storage.store(value254) shouldBe 254
        storage.getById(254) shouldNotBe value254 shouldHaveLength 0

        val value255 = "255"
        storage.store(value255) shouldBe 255
        storage.getById(255) shouldNotBe value255 shouldHaveLength 0

        val value256 = "256"
        storage.store(value256) shouldBe 256
        storage.getById(256) shouldBe value256
    }

    @Test
    fun should_insert_again_after_fake_insert() {
        var counter = 0
        repeat(254) { storage.store((counter++).toString()) }

        val value254 = "254"
        storage.store(value254) shouldBe 254

        val value255 = "255"
        storage.store(value255) shouldBe 255

        storage.store(value254) shouldBe 256
        storage.getById(256) shouldBe value254

        storage.store(value255) shouldBe 257
        storage.getById(257) shouldBe value255
    }

    @Test
    fun should_reset_on_1024_items() {
        val maxStorageSize = 1024

        val ignoredIndexes = listOf(254, 255, 510, 511, 766, 767, 1022)

        // fill storage up to 1023
        for (i in 0..<(maxStorageSize - 1)) {
            val value = Random.nextLong().toString()

            val id = storage.store(value)

            id shouldBe i
            when (id) {
                in ignoredIndexes -> storage.getById(id) shouldNotBe value shouldHaveLength 0
                else -> storage.getById(id) shouldBe value
            }
        }

        // verify that can insert one more
        val lastId = storage.store(Random.nextLong().toString())
        lastId shouldBe maxStorageSize - 1

        // insert one to overflow and reset the storage
        val valueAfterReset = Random.nextLong().toString()
        storage.store(valueAfterReset) shouldBe 0
        storage.getById(0) shouldBe valueAfterReset

        shouldThrow<IndexOutOfBoundsException> { storage.getById(lastId) }
    }
}
