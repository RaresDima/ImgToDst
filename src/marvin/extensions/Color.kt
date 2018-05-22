package marvin.extensions


import marvin.color.MarvinColor


typealias Color = MarvinColor


/**
 * toString function for the [Color] type. Returns a [String] representation of the object.
 *
 * @return A [String] representation of the current [Color].
 */
fun Color.repr() : String = "Color (R $red ; G $green ; B $blue)"


/**
 * Equality infix function for [Color]s.
 *
 * @param other The [Color] to compare the current color to.
 *
 * @return Whether the 2 colors are the same or not.
 */
infix fun Color.eq(other: Color): Boolean = (this.red == other.red) and (this.green == other.green) and (this.blue == other.blue)


/**
 * Difference infix function for [Color]s.
 *
 * @param other The [Color] to compare the current color to.
 *
 * @return Whether the 2 colors are different or not.
 */
infix fun Color.neq(other: Color): Boolean = !(this eq other)


/**
 * Checks if this [Color] is in the given [List] of colors.
 *
 * @param list The list of colors to check.
 *
 * @return Whether or not this color is in [list].
 */
infix fun Color.isNotIn(list: List<Color>) : Boolean = list.none { it eq this }


