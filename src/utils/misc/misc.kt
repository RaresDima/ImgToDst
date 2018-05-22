package utils.misc


/**
 * Generic extension function acting as a pipe, or reverse application operator to make function calls more readable.
 *
 * This code:
 *
 * func3(func2(func1(input))
 *
 * Can be rewritten as:
 *
 *  func1(input) then ::func2 then ::func3
 *
 *  @param func The function to apply next.
 *
 *  @return The result of the function application
 */
infix fun <I, O> I.then(func: (I) -> O) : O = func(this)


/**
 * 2 value comparison function using a custom key function.
 *
 * @param a   The first  value to be compared.
 * @param b   The second value to be compared.
 * @param key The function that will be applied to both [a] and [b] and the return values used for comparison.
 */
fun <T> minBy(a: T, b: T, key: (T) -> Double) : T = if (key(a) >= key(b)) a else b
