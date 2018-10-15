package com.izettle.kotlin

import org.junit.Assert.fail
import org.junit.Test

class ValueChecksTest {

    @Test
    fun `should return null when one argument is null`() {
        val name: String? = null
        val age: Int? = 40
        whenNoneNull(name, age) { first, second ->
            fail("Block was called with $first, $second")
        }
    }

    @Test
    fun `should call block when nothing is null`() {
        val name: String? = "Test"
        val age: Int? = 40
        whenNoneNull(name, age) { first, second ->
            println("Block was called with $first, $second")
        } ?: fail("Block was not called")
    }
}
