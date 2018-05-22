package utils.bool


/**
 * Returns 0 if the current [Boolean] is false, 1 if it is true.
 *
 * @return 0 if the current [Boolean] is false, 1 if it is true.
 */
fun Boolean.toInt() : Int = if (this) 1 else 0
