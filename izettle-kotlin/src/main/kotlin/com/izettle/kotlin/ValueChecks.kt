package com.izettle.kotlin

/**
 * Takes any two nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * Usage:
 * ```
 * val field1: String? = null
 * val field2: Int? = null
 * whenNoneNull(field1, field2) { f1, f2 ->
 *    f1.toUppercase() // non-nullable f1
 *    f2.toString()    // non-nullable f2
 * }
 * ```
 */
inline fun <T1 : Any, T2 : Any, R : Any> whenNoneNull(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

/**
 * Takes any 3 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: (T1, T2, T3) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

/**
 * Takes any 4 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}

/**
 * Takes any 5 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    block: (T1, T2, T3, T4, T5) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) block(p1, p2, p3, p4, p5) else null
}

/**
 * Takes any 6 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    p6: T6?,
    block: (T1, T2, T3, T4, T5, T6) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null && p6 != null)
        block(p1, p2, p3, p4, p5, p6) else null
}

/**
 * Takes any 7 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    p6: T6?,
    p7: T7?,
    block: (T1, T2, T3, T4, T5, T6, T7) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null && p6 != null && p7 != null)
        block(p1, p2, p3, p4, p5, p6, p7) else null
}

/**
 * Takes any 8 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    p6: T6?,
    p7: T7?,
    p8: T8?,
    block: (T1, T2, T3, T4, T5, T6, T7, T8) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null &&
        p6 != null && p7 != null && p8 != null
    ) block(p1, p2, p3, p4, p5, p6, p7, p8) else null
}

/**
 * Takes any 9 nullable inputs and calls the specified block with non-null asserted versions of those inputs
 *
 * @see whenNoneNull
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, T9 : Any, R : Any> whenNoneNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    p6: T6?,
    p7: T7?,
    p8: T8?,
    p9: T9?,
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null &&
        p6 != null && p7 != null && p8 != null && p9 != null
    ) block(p1, p2, p3, p4, p5, p6, p7, p8, p9) else null
}
