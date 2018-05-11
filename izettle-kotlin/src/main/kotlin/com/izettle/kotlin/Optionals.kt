package com.izettle.kotlin

import java.util.Optional

/**
 * Convert a Java Optional to a Kotlin nullable type to avoid handling Optionals in Kotlin.
 *
 * Usage:
 * ```
 * optional.asNullable()?.let { "has value $it" } ?: "empty"
 * ```
 *
 * @see Optional
 */
fun <T : Any> Optional<T>.asNullable(): T? = this.orElse(null)
