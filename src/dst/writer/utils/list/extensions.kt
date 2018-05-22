package dst.writer.utils.list


import utils.integer.pow
import utils.misc.then


/**
 * Returns the decimal value corresponding to the given LST balanced ternary number.
 *
 * @param bt The [List] of trits of the BT number to be converted to decimal.
 *
 * @return The decimal number corresponding to [bt].
 */
fun List<Int>.balancedTernaryToDecimal() : Int  = withIndex().sumBy { (pwr, trt) -> trt * (3 pow pwr) }


/**
 * Returns the [Int] value corresponding to the base 2 number stored in the current [List].
 *
 * @return The [Int] value corresponding to the base 2 number stored in the current [List].
 */
fun List<Int>.binaryToDecimal() : Int = asReversed().mapIndexed { ind, bit -> bit * (2 pow ind) } .sum()


/**
 * Returns a [ByteArray] of bytes that is ready to be written to a DST file.
 *
 * Converts a [List] of [List] of [Int] representing a [List] ob base 2 numbers to the corresponding [ByteArray].
 *
 * @return A [ByteArray] of bytes that is ready to be written to a DST file.
 */
fun List<List<Int>>.toDstInstructionBytes() : ByteArray = this.map { it.binaryToDecimal().toByte() }.toByteArray()


/**
 * Prints the current instruction
 */
fun List<List<Int>>.printDstInstructionBits() {
        println("   Bit |   7   6   5   4   3   2   1   0 ".replace(' ', ' '))
        println("-------+---------------------------------")
        for((i, byte) in this.withIndex()) {
                print("Byte $i |   ")
                for (bit in byte)
                        print("$bit   ")
                println()
        }
}