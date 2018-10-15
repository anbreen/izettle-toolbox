package com.izettle.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.Optional

class OptionalsTest {

    @Test
    fun `should convert optional to nullable containing value`() {
        val field: String? = "Test"

        val optional = Optional.ofNullable(field)

        val result = optional.asNullable()?.let { "has value $it" } ?: "empty"

        assertThat(result).isEqualTo("has value Test")
    }

    @Test
    fun `should convert optional to nullable with null value`() {
        val field: String? = null

        val optional = Optional.ofNullable(field)

        val result = optional.asNullable()?.let { "has value $it" } ?: "empty"

        assertThat(result).isEqualTo("empty")
    }
}
