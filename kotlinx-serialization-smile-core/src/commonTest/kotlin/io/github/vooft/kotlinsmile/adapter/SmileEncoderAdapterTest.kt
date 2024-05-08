package io.github.vooft.kotlinsmile.adapter

import io.github.vooft.kotlinsmile.Project
import io.github.vooft.kotlinsmile.User
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly

class SmileEncoderAdapterTest : ShouldSpec({
    should("should encode primitive") {
        val result = encodeToList(1)
        result shouldContainExactly listOf(1)
    }

    should("should encode data class") {
        val obj = Project("kotlinx.serialization", User("kotlin"), 9000)
        val result = encodeToList(obj)
        result shouldContainExactly listOf("kotlinx.serialization", "kotlin", 9000)
    }

})
