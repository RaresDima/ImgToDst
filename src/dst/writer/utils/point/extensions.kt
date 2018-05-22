package dst.writer.utils.point


import clustering.supervised.Point
import dst.writer.utils.integer.decimalToBalancedTernary
import utils.bool.toInt


/**
 * Returns a list of 3 lists, each having 8 values of 0 or 1, corresponding the to 3-byte DST instruction specification.
 *
 * @param jmp Whether this point corresponds to a jump stitch or not.
 * @param csw Whether this point corresponds to a color switch or not.
 *
 * @return A list of 3 lists, each having 8 values of 0 or 1, corresponding the to 3-byte DST instruction specification.
 */
fun Point.toDstInstructionBits(jmp: Boolean = false, csw: Boolean = false) : List<List<Int>> {

        val x = this[1].decimalToBalancedTernary()
        val y = this[0].decimalToBalancedTernary()

        val result = List(3) { byte ->
                List(8) { pos ->
                        when {
                                (byte == 2) and (pos == 6) -> 1                         // set bit -2
                                (byte == 2) and (pos == 7) -> 1                         // set bit -1

                                (byte == 2) and (pos == 0) -> jmp.toInt()               // jump
                                (byte == 2) and (pos == 1) -> csw.toInt()               // color switch

                                (byte == 0) and (pos == 0) -> (y[0] ==  1).toInt()      // y+1
                                (byte == 0) and (pos == 1) -> (y[0] == -1).toInt()      // y-1

                                (byte == 1) and (pos == 0) -> (y[1] ==  1).toInt()      // y+3
                                (byte == 1) and (pos == 1) -> (y[1] == -1).toInt()      // y-3

                                (byte == 0) and (pos == 2) -> (y[2] ==  1).toInt()      // y+9
                                (byte == 0) and (pos == 3) -> (y[2] == -1).toInt()      // y-9

                                (byte == 1) and (pos == 2) -> (y[3] ==  1).toInt()      // y+27
                                (byte == 1) and (pos == 3) -> (y[3] == -1).toInt()      // y-27

                                (byte == 2) and (pos == 2) -> (y[4] ==  1).toInt()      // y+81
                                (byte == 2) and (pos == 3) -> (y[4] == -1).toInt()      // y-81

                                (byte == 0) and (pos == 7) -> (x[0] ==  1).toInt()      // x+1
                                (byte == 0) and (pos == 6) -> (x[0] == -1).toInt()      // x-1

                                (byte == 1) and (pos == 7) -> (x[1] ==  1).toInt()      // x+3
                                (byte == 1) and (pos == 6) -> (x[1] == -1).toInt()      // x-3

                                (byte == 0) and (pos == 5) -> (x[2] ==  1).toInt()      // x+9
                                (byte == 0) and (pos == 4) -> (x[2] == -1).toInt()      // x-9

                                (byte == 1) and (pos == 5) -> (x[3] ==  1).toInt()      // x+27
                                (byte == 1) and (pos == 4) -> (x[3] == -1).toInt()      // x-27

                                (byte == 2) and (pos == 5) -> (x[4] ==  1).toInt()      // x+81
                                (byte == 2) and (pos == 4) -> (x[4] == -1).toInt()      // x-81

                                else -> -1 // Unreachable
                        }
                }
        }

        return result
}


/**
 * Returns a new [Point] that represents the relative position of the current [Point] to [base].
 *
 * @param base The [Point] to which the relative position is desired.
 *
 * @return A new [Point] that represents the relative position of the current [Point] to [base].
 */
infix fun Point.relativeTo(base: Point) : Point = Point(coordList().zip(base.coordList()).map { (x, xBase) -> x - xBase })