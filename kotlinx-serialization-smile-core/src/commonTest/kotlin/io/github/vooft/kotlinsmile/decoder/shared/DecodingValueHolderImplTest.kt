package io.github.vooft.kotlinsmile.decoder.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import kotlin.random.Random
import kotlin.test.Test

class DecodingValueHolderImplTest {
    private val holder = DecodingValueHolderImpl()

    @Test
    fun should_store_and_retrieve_value() {
        val value = Random.nextLong().toString()

        val id = holder.store(value)

        id shouldBe 0
        holder.get(id) shouldBe value
    }

    @Test
    fun should_store_and_retrieve_multiple_values() {
        val values = 10
        for (i in 0..values) {
            val value = Random.nextLong().toString()

            val id = holder.store(value)

            id shouldBe i
            holder.get(id) shouldBe value
        }
    }

    @Test
    fun should_store_duplicate_under_same_id() {
        val value = Random.nextLong().toString()

        val id1 = holder.store(value)
        val id2 = holder.store(value)

        id1 shouldBe 0
        id2 shouldBe 0
        holder.get(id1) shouldBe value
    }

    @Test
    fun should_fake_insert_on_254_255() {
        var counter = 0
        repeat(254) { holder.store((counter++).toString()) }

        val value254 = "254"
        holder.store(value254) shouldBe 254
        holder.get(254) shouldNotBe value254 shouldHaveLength 65

        val value255 = "255"
        holder.store(value255) shouldBe 255
        holder.get(255) shouldNotBe value255 shouldHaveLength 65

        val value256 = "256"
        holder.store(value256) shouldBe 256
        holder.get(256) shouldBe value256
    }

    @Test
    fun should_insert_again_after_fake_insert() {
        var counter = 0
        repeat(254) { holder.store((counter++).toString()) }

        val value254 = "254"
        holder.store(value254) shouldBe 254

        val value255 = "255"
        holder.store(value255) shouldBe 255

        holder.store(value254) shouldBe 256
        holder.get(256) shouldBe value254

        holder.store(value255) shouldBe 257
        holder.get(257) shouldBe value255
    }

    @Test
    fun should_reset_on_1024_items() {
        val maxStorageSize = 1024

        val ignoredIndexes = listOf(254, 255, 510, 511, 766, 767, 1022)

        // fill storage up to 1023
        for (i in 0..<(maxStorageSize - 1)) {
            val value = Random.nextLong().toString()

            val id = holder.store(value)

            id shouldBe i
            when (id) {
                in ignoredIndexes -> holder.get(id) shouldNotBe value shouldHaveLength 65
                else -> holder.get(id) shouldBe value
            }
        }

        // verify that can insert one more
        val lastId = holder.store(Random.nextLong().toString())
        lastId shouldBe maxStorageSize - 1

        // insert one to overflow and reset the storage
        val valueAfterReset = Random.nextLong().toString()
        holder.store(valueAfterReset) shouldBe 0
        holder.get(0) shouldBe valueAfterReset

        shouldThrow<IndexOutOfBoundsException> { holder.get(lastId) }
    }
}
