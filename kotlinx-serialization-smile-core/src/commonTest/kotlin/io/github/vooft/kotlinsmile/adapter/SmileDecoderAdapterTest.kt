package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.Project
import io.github.vooft.kotlinsmile.User
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class SmileDecoderAdapterTest : ShouldSpec({
    should("should decode primitive value") {
        val actual = decodeFromList<Int>(listOf(1))
        actual shouldBe 1
    }

    should("should decode data class") {
        val actual = decodeFromList<Project>(listOf("kotlinx.serialization", "kotlin", 9000))
        actual shouldBe Project("kotlinx.serialization", User("kotlin"), 9000)
    }
})
