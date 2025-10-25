package ch.tutteli.atrium.api.infix.en_GB

import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.specs.fun1
import ch.tutteli.atrium.specs.withNullableSuffix
import kotlin.test.Test

class IterableNotToHaveElementsOrAnyExpectationsTest :
    ch.tutteli.atrium.specs.integration.AbstractIterableNotToHaveElementsOrAnyExpectationsTest(
        fun1(Expect<Iterable<Double>>::notToHaveElementsOrAny),
        fun1(Expect<Iterable<Double?>>::notToHaveElementsOrAny).withNullableSuffix()
    ) {

    @Suppress("AssignedValueIsNeverRead")
    @Test
    fun ambiguityTest() {
        var list: Expect<List<Number>> = expect(listOf(1.1))
        var nList: Expect<Set<Number?>> = expect(setOf(1.1))
        var subList: Expect<ArrayList<out Number>> = expect(arrayListOf(1.1))
        var star: Expect<Collection<*>> = expect(listOf(1.1, "asdf"))

        list = list notToHaveElementsOrAny {
            it notToEqual 2.1
        }
        nList = nList notToHaveElementsOrAny {
            it toEqual 1.1
        }
        subList = subList notToHaveElementsOrAny {
            it toEqual 1.1
        }
        star = star notToHaveElementsOrAny {
            it notToEqual 2.1
        }
    }
}
