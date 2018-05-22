package utils.long


/**
 * [Long] extension function for computing power.
 *
 * @param exp The power to which to raise the current [Long]
 *
 * @return The current [Long] raise to the power of [exp]
 */
fun Long.pow(exp: Long) : Long {
        var res = this
        for (i in 1..exp)
                res *= this
        return res
}


/**
 * [Long] extension function for computing square root.
 *
 * @return The square root of the current [Long].
 */
fun Long.sqrt() : Double = kotlin.math.sqrt(this.toDouble())
