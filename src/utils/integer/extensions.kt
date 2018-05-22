package utils.integer


/**
 * [Int] extension function for computing power.
 *
 * @param exp The power to which to raise the current [Int]
 *
 * @return The current [Int] raise to the power of [exp]
 */
infix fun Int.pow(exp: Int) : Int {
        var res = if (exp == 0) 1 else this
        for (i in 2..exp)
                res *= this
        return res
}


/**
 * [Int] extension function for computing square root.
 *
 * @return The square root of the current [Int].
 */
fun Int.sqrt() : Double = kotlin.math.sqrt(this.toDouble())
