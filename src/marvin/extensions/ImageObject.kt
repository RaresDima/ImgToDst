package marvin.extensions


import clustering.supervised.Point
import utils.list.concat


/**
 * Class modeling an object in an image. An object is a connected mass of pixels of the same [Color].
 *
 * On creation the object containing the point at the starting position is parsed using a flood-fill algorithm to create
 * the set of edge/contour/frontier points, and the set of interior points.
 *
 * @param image                 The [Image] to extract the [ImageObject] from.
 * @param startPoint            The starting [Point] of the flood-fill algorithm.
 * @param noUnmark   (Optional) Whether or not to restore the original color of the marked pixels. Defaults to false.
 * @param verbose    (Optional) Whether or not to print the current object index, the current iteration number and open
 *                              set size during runtime.
 * @param objIndex   (Optional) The index of the current [ImageObject] in the image. Only used when verbose mode is
 *                              enabled, used for clarity of output.
 *
 * @property contour  The [List] of points that are on the frontier/edge of this [ImageObject].
 * @property interior The [List] of points that are inside/not on the frontier of this [ImageObject].
 */
class ImageObject(image: Image, startPoint: Point, noUnmark: Boolean = false, verbose: Boolean = false, objIndex: Int = -1) {
        val contour:  List<Point>
        val interior: List<Point>

        init {
                val startColor : Color = image[startPoint]
                val markedColor: Color = Color((startColor.red + 1) % 255, startColor.green, startColor.blue)

                val mutContour : MutableList<Point> = mutableListOf()
                val mutInterior: MutableList<Point> = mutableListOf()

                val openSet    : MutableList<Point> = startPoint.neighbours().toMutableList()

                fun Point.isOutsideImage(): Boolean =
                        (this[0] < 0) or (this[0] >= image.width) or (this[1] < 0) or (this[1] >= image.height)

                fun Point.mark()                    { image[this] =   markedColor }
                fun Point.isNotMarked()   : Boolean = image[this] neq markedColor

                fun Point.isInterior()    : Boolean = neighbours().filter { !it.isOutsideImage() } .all { image[it] eq startColor }
                fun Point.isExterior()    : Boolean = image[this] neq startColor
                fun Point.isInside()      : Boolean = image[this] eq  startColor

                fun Point.unmarkedInternalNeighbours() : List<Point> =
                        neighbours().filter { !it.isOutsideImage() }.filter { it.isNotMarked() and it.isInside() }

                if (startPoint.isInterior()) mutInterior.add(startPoint) else mutContour.add(startPoint)

                var iter = 0
                while (openSet.isNotEmpty()) {
                        // Get the next point.
                        val currentPoint = openSet.first()

                        // Move it to the visited set.
                        openSet.remove(currentPoint)

                        // Skip the current point if it is exterior to the object or outside the debug.main image.
                        if (currentPoint.isOutsideImage()) continue
                        if (currentPoint.isExterior())     continue

                        if (verbose) println("OBJECT: $objIndex \t ITER: ${iter++} \t Open Set: ${openSet.count()}")

                        // Add point to either interior or contour.
                        if (currentPoint.isInterior()) mutInterior.add(currentPoint) else mutContour.add(currentPoint)
                        currentPoint.mark()

                        // Add the non-visited internal neighbours to the open set.
                        openSet.addAll(currentPoint.unmarkedInternalNeighbours())

                }

                if (verbose) println("OBJECT: $objIndex \t ITER: $iter \t Open Set: ${openSet.count()}")

                contour  = mutContour.toList()
                interior = mutInterior.toList()

                if (!noUnmark) allPoints().forEach { image[it] = startColor }
        }


        /**
         * Returns the complete [List] of [Point]s of the current [ImageObject] containing both [contour] and [interior].
         *
         * @return The complete [List] of [Point]s of the current [ImageObject].
         */
        fun allPoints() : List<Point> = contour concat interior

        /**
         * Returns the sorted [List] of [Point]s of the current [ImageObject] containing both [contour] and [interior].
         *
         * The sorting takes place row-first, column second.
         *
         * @return The sorted complete [List] of [Point]s of the current [ImageObject].
         */
        fun allPointsSorted() : List<Point> = allPoints().sortedBy { it[0] * 1000 + it[1] }


        /**
         * Returns the number of pixels in this [ImageObject]
         *
         * @return The number of pixels contained in this [ImageObject].
         */
        fun area() : Int = allPoints().count()
}