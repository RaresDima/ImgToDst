package utils.list


/**
 * [List] extension function for returning a random element from the list.
 *
 * @return A random element from the current [List], each with equal probability.
 */
fun <T> List<T>.randomChoice() : T = this[java.util.concurrent.ThreadLocalRandom.current().nextInt(0, this.count())]


/**
 * [List] extension function for returning the cumulative sum of the list.
 *
 * @return A [List] containing the cumulative sum of the current list.
 */
fun List<Int>.cumSum() : List<Long> {
        val result = mutableListOf(this[0].toLong())
        for (i in 1 until this.count())
                result.add(result[i - 1] + this[i].toLong())
        return result.toList()
}


/**
 * [List] extension function for returning a weighted random element from the list.
 *
 * Any [Int] weights can be used.
 *
 * @param weights The [List] of weights to be used.
 *
 * @return The chosen element.
 */
fun <T> List<T>.weightedChoice(weights: List<Int>) : T {
        val cumsum  = weights.cumSum()
        val randval = java.util.concurrent.ThreadLocalRandom.current().nextLong(0, cumsum.last())
        return this[cumsum.indexOf(cumsum.first { it > randval })]
}


/**
 * [List] extension function for concatenating the current and [other] Lists into a new List.
 *
 * @param other The list to be concatenated to the current list.
 *
 * @return The new list containing the elements of both lists.
 */
infix fun <T> List<T>.concat(other: List<T>) : List<T> = this.toMutableList().also { it.addAll(other) }


