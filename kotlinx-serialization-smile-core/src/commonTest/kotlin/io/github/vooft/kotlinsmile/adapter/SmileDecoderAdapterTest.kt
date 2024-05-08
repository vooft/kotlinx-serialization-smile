package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.Project
import io.github.vooft.kotlinsmile.User
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SmileDecoderAdapterTest {
    @Test
    fun `should decode primitive value`() {
        val actual = decodeFromList<Int>(listOf(1))
        actual shouldBe 1
    }

    @Test
    fun `should decode data class`() {
        val actual = decodeFromList<Project>(listOf("kotlinx.serialization", "kotlin", 9000))
        actual shouldBe Project("kotlinx.serialization", User("kotlin"), 9000)
    }
}
