package ch.tutteli.atrium.specs.integration

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.specs.*
import ch.tutteli.atrium.translations.DescriptionCollectionExpectation
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite

abstract class AbstractCollectionExpectationsTest(
    isEmpty: Fun0<Collection<Int>>,
    isNotEmpty: Fun0<Collection<Int>>,
    sizeFeature: Feature0<Collection<Int>, Int>,
    size: Fun1<Collection<Int>, Expect<Int>.() -> Unit>,
    describePrefix: String = "[Atrium] "
) : Spek({

    include(object : SubjectLessSpec<Collection<Int>>(
        describePrefix,
        isEmpty.forSubjectLessTest(),
        isNotEmpty.forSubjectLessTest(),
        sizeFeature.forSubjectLessTest().withFeatureSuffix(),
        size.forSubjectLessTest { toBeGreaterThan(2) }
    ) {})

    include(object : AssertionCreatorSpec<Collection<Int>>(
        describePrefix, listOf(999),
        size.forExpectationCreatorTest("$toEqualDescr: 1") { toEqual(1) }
    ) {})

    fun describeFun(vararg pairs: SpecPair<*>, body: Suite.() -> Unit) =
        describeFunTemplate(describePrefix, pairs.map { it.name }.toTypedArray(), body = body)

    val empty = DescriptionCollectionExpectation.EMPTY.getDefault()
    val sizeDescr = DescriptionCollectionExpectation.SIZE.getDefault()

    describeFun(isEmpty, isNotEmpty) {
        val isEmptyFun = isEmpty.lambda
        val isNotEmptyFun = isNotEmpty.lambda

        context("collection is empty") {
            it("${isEmpty.name} - does not throw") {
                expect(emptyList<Int>() as Collection<Int>).isEmptyFun()
            }
            it("${isNotEmpty.name} - throws an AssertionError") {
                expect {
                    expect(emptyList<Int>() as Collection<Int>).isNotEmptyFun()
                }.toThrow<AssertionError> { messageToContain("$notToBeDescr: $empty") }
            }
        }

        context("collection is not empty") {
            it("${isEmpty.name} - throws an AssertionError") {
                expect {
                    expect(listOf(1, 2) as Collection<Int>).isEmptyFun()
                }.toThrow<AssertionError> { messageToContain("$toBeDescr: $empty") }
            }
            it("${isNotEmpty.name} - does not throw") {
                expect(listOf(1) as Collection<Int>).isNotEmptyFun()
            }
        }
    }

    describeFun(sizeFeature, size) {
        val sizeFunctions = unifySignatures(sizeFeature, size)

        context("list with two entries") {
            sizeFunctions.forEach { (name, sizeFun, _) ->
                it("$name - is greater than 1 holds") {
                    expect(listOf(1, 2) as Collection<Int>).sizeFun { toBeGreaterThan(1) }
                }
                it("$name - is less than 1 fails") {
                    expect {
                        expect(listOf(1, 2) as Collection<Int>).sizeFun { toBeLessThan(1) }
                    }.toThrow<AssertionError> {
                        messageToContain("$sizeDescr: 2")
                    }
                }
            }
        }
    }

})
