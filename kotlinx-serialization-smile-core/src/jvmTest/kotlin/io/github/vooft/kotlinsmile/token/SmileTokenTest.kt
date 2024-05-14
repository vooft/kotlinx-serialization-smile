package io.github.vooft.kotlinsmile.token

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import kotlin.reflect.KClass
import kotlin.test.Test

class SmileTokenTest {
    @Test
    fun `should have all value tokens in a list`() {
        val allSubclasses = SmileValueToken::class.findSubclassesObjects()
        SmileTokensHolder.VALUE_TOKENS shouldContainExactlyInAnyOrder allSubclasses
    }

    @Test
    fun `should have all key tokens in a list`() {
        val allSubclasses = SmileKeyToken::class.findSubclassesObjects()
        SmileTokensHolder.KEY_TOKENS shouldContainExactlyInAnyOrder allSubclasses
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
        val allSubclasses = findSubclassesObjects()
        val seenRanges = mutableMapOf<Int, SmileToken>()

        for (subclass in allSubclasses) {
            for (i in subclass.tokenRange) {
                val existing = seenRanges.put(i, subclass)
                require(existing == null) { "Token $subclass intersects with $existing at ${i.toString(16)}" }
            }
        }
    }

    private fun <T: SmileToken> KClass<T>.findSubclassesObjects(): Set<T> = buildSet {
        for (subclass in sealedSubclasses) {
            val subclassObject = subclass.objectInstance
            if (subclassObject != null) {
                add(subclassObject as T)
            } else {
                require(subclass.isSealed) { "Class $subclass is not sealed" }
                addAll(subclass.findSubclassesObjects())
            }
        }
    }
}
