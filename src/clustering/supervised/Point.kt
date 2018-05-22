package clustering.supervised

import utils.integer.pow
import utils.integer.sqrt
import marvin.extensions.Color
import utils.misc.then


/** Class describing an n-dimensional point.
 *
 * @property coords The n coordinates of the current [Point].
 */
class Point {
        private var coords: MutableList<Int> = mutableListOf()


        /**
         * Constructor initializing [dim] coordinates to 0.
         *
         * @param dim The number of dimensions for the current [Point]
         */
        constructor(dim: Int) { coords = MutableList(dim) { 0 } }

        /**
         * Constructor initializing the current [Point] coords to the ones given in [coords]
         *
         * @param coords The coordinates to initialize the point with.
         */
        constructor(coords: List<Int>) { this.coords = coords.toMutableList() }

        /**
         * Constructor initializing the current [Point]s [coords] to the values given as arguments.
         *
         * @param coords The values to be assigned as coordinates.
         */
        constructor(vararg coords: Int) { this.coords = coords.toMutableList() }

        /**
         * Copy constructor.
         *
         * @param point The [Point] to copy from.
         */
        constructor(point: Point) { coords = point.coords.toMutableList() }

        /**
         * Constructor for a point representing a [Color] in the RGB space.
         *
         * @param color The [Color] to construct the [Point] from.
         */
        constructor(color: Color) { coords = mutableListOf(color.red, color.green, color.blue) }


        /**
         * Converts the first 3 coordinates of the current [Point] to an RGB [Color].
         *
         * @return The RGB [Color] corresponding to the current [Point].
         */
        fun toColor() : Color = Color(coords[0], coords[1], coords[2])


        /**
         * The [Point] [[coord]] operator. Returns the coordinate at the specified index.
         *
         * @param coord The index of the coordinate to be returned.
         *
         * @return The coordinate at position [coord]
         */
        operator fun get(coord: Int) = coords[coord]

        /**
         * The [Point] [[coord]] = [value] operator. Sets the coordinate at the given position to the given value.
         *
         * @param coord The position of the coordinate to be set.
         * @param value The new vlue to be given to the coordinate.
         */
        operator fun set(coord: Int, value: Int) { coords[coord] = value }


        /**
         * The [Point] eq [other] operator. Returns whether to 2 points are equal or not.
         *
         * @param other The [Point] to compare the current [Point] to.
         *
         * @return Whether the 2 points are equal or not.
         */
        infix fun eq(other: Point) : Boolean = coords.zip(other.coords).all { (x1, x2) -> x1 == x2 }


        /**
         * Infix function returning the squared euclidean distance to [other]. Faster than [distanceTo].
         *
         * @param other The [Point] to which the distance should be computed.
         *
         * @return The squared euclidean distance to [other].
         */
        infix fun sqrDistanceTo(other: Point) : Int = coords.zip(other.coords) { x1, x2 -> x1.minus(x2).pow(2) }.sum()

        /**
         * Infix function returning the euclidean distance to [other].
         *
         * Slower than [sqrDistanceTo]. Only use when necessary.
         *
         * @param other The [Point] to which the distance should be computed.
         *
         * @return The euclidean distance to [other].
         */
        infix fun distanceTo(other: Point) : Double = this.sqrDistanceTo(other).sqrt()


        /**
         * The default toString function of the [Point] class.
         *
         * @return The [String] representation of the current [Point].
         */
        override fun toString() : String = "Point $coords"


        /**
         * Returns the number of coordinates of the current [Point].
         *
         * @return The number of coordinates of the current [Point].
         */
        fun dimensions() : Int = coords.count()

        /**
         * Returns an immutable [List] copy of the [List] of coordinates.
         *
         * @return An immutable [List] copy of the [List] of coordinates.
         */
        fun coordList() : List<Int> = coords.toList()

        /**
         * The average value of all the [Point]s coordinates.
         *
         * @return The average value of [coords].
         */
        fun avg() : Double = coords.average()


        /**
         * Do not call unless the point is in 2 dimensions. Returns the 8 neighbors of the current [Point]
         *
         * @return The 8 neighbors of the current [Point]: up, down, left, right and diagonals.
         */
        fun neighbours() : List<Point> = listOf(
                listOf(this[0] - 1, this[1] + 1) then ::Point,
                listOf(this[0],     this[1] + 1) then ::Point,
                listOf(this[0] + 1, this[1] + 1) then ::Point,

                listOf(this[0] - 1, this[1])     then ::Point,
                listOf(this[0] + 1, this[1])     then ::Point,

                listOf(this[0] - 1, this[1] - 1) then ::Point,
                listOf(this[0],     this[1] - 1) then ::Point,
                listOf(this[0] + 1, this[1] - 1) then ::Point

        )
}