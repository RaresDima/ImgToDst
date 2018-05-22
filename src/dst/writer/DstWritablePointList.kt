package dst.writer


import clustering.supervised.Point
import dst.writer.utils.integer.decimalToBalancedTernary
import dst.writer.utils.list.toDstInstructionBytes
import dst.writer.utils.point.relativeTo
import dst.writer.utils.point.toDstInstructionBits
import utils.list.concat
import utils.misc.then
import utils.octet.toBinary
import java.io.File


/**
 * Class describing a succession of points in a .DST design.
 *
 * @param initialPoints The succession of [Point]s describing the design, in absolute coordinates.
 *
 * @property points The [Point]s describing the design, each [Point]'s coordinates are relative to the previous one's.
 *                  The first point's coordinates are relative to the point (0, 0).
 *                  All the points are translated by (+120, -120) so the center point (0, 0) is in the center of the
 *                  plane, not the upper left corner.
 */
class DstWritablePointList(initialPoints: List<Point>) {
        val points: MutableList<Point> =
                (mutableListOf(Point(0, 0)) concat initialPoints.toMutableList())
                        .map { Point(-it[0] + 120, it[1] - 120) }
                        .zipWithNext { oldPoint, newPoint -> newPoint relativeTo oldPoint }
                        .apply { this[0][0] *= -1 }
                        .toMutableList()


        /**
         * Returns the original [List] of [Point] that the [DstWritablePointList] was created with.
         *
         * @return The original [List] of [Point] that the [DstWritablePointList] was created with.
         */
        fun originalPoints() : List<Point> {
                val result: List<Point> = points.toList()

                for (i in 1..result.lastIndex) {
                        result[i][0] += result[i-1][0]
                        result[i][1] += result[i-1][1]
                }

                return result
        }


        /**
         * Writes the .DST file corresponding to this [DstWritablePointList] with the name given by [filename].
         *
         * If the file does not exist, it will be created.
         *
         * @param filename The name of the file to be written to.
         */
        fun plotPointsToFile(filename: String) {
                val outFile = File(filename).apply { createNewFile() }

                val header  = List<Byte>(512) { 0x20 } .toList()
                val data    = points.flatMap { it.toDstInstructionBits().toDstInstructionBytes().toList() } .toList()
                val footer  = listOf(0x00.toByte(), 0x00.toByte(), 0xF3.toByte())

                outFile.writeBytes( (header concat data concat footer).toByteArray() )
        }


}