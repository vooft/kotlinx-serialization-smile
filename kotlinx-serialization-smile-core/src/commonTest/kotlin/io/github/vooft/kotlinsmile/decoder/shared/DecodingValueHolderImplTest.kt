package io.github.vooft.kotlinsmile.decoder.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
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
    fun should_reset_on_1024_items() {
        val maxStorageSize = 1024

        // fill storage up to 1023
        for (i in 0..<(maxStorageSize - 1)) {
            val value = Random.nextLong().toString()

            val id = holder.store(value)

            id shouldBe i
            holder.get(id) shouldBe value
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
