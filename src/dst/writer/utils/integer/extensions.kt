package dst.writer.utils.integer

import kotlin.math.absoluteValue


/**
 * Returns the 5-trit balanced ternary LST(least significant trit first) representation.
 *
 * @return The [List] of 5 trits of the balanced ternary LST(least significant trit first)
 *         representation.
 */
fun Int.decimalToBalancedTernary() : List<Int> {

        fun _decimalToBalancedTernary(n: Int) : List<Int> = when {
                n     == 0 -> listOf()
                n % 3 == 0 -> listOf( 0) + _decimalToBalancedTernary(n / 3)
                n % 3 == 1 -> listOf( 1) + _decimalToBalancedTernary(n / 3)
                n % 3 == 2 -> listOf(-1) + _decimalToBalancedTernary((n + 1) / 3)

                else -> listOf() // Unreachable
        }

        return _decimalToBalancedTernary(this.absoluteValue)
                .toMutableList()
                .apply { addAll(List(5 - count()) { 0 }) }
                .let { if (this < 0) it.map { -it } else it }
}