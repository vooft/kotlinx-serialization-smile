package io.github.vooft.kotlinsmile.token

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import kotlin.reflect.KClass
import kotlin.test.Test

class SmileTokenTest {
    @Test
    fun `should have all tokens in a list`() {
        val allSubclasses = SmileToken::class.findSubclassesDataObjects()
        SmileToken.ALL_TOKENS shouldContainExactlyInAnyOrder allSubclasses
    }

    @Test
    fun `should not have intersections in value token ranges`() {
        SmileValueToken::class.requireNoIntersection()
    }

    @Test
    fun `should not have intersections in key token ranges`() {
        SmileKeyToken::class.requireNoIntersection()
    }

    private fun <T: SmileToken> KClass<T>.requireNoIntersection() {
        val allSubclasses = findSubclassesDataObjects()
        val seenRanges = mutableMapOf<Int, SmileToken>()

        for (subclass in allSubclasses) {
            for (i in subclass.tokenRange) {
                val existing = seenRanges.put(i, subclass)
                require(existing == null) { "Token $subclass intersects with $existing at ${i.toString(16)}" }
            }
        }
    }

    private fun <T: SmileToken> KClass<T>.findSubclassesDataObjects(): List<SmileToken> = buildList {
        for (subclass in sealedSubclasses) {
            val subclassObject = subclass.objectInstance
            if (subclassObject != null) {
                add(subclassObject as SmileToken)
            } else {
                require(subclass.java.isInterface) { "Class $subclass is not interface" }
                require(subclass.isSealed) { "Interface $subclass is not sealed" }
                addAll(subclass.findSubclassesDataObjects())
            }
        }
    }
}
