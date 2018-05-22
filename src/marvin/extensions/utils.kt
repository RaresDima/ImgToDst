package marvin.extensions


/**
 * Object containing 2 static constant properties, [ON] and [OFF].
 *
 * A dark and a light gray to symbolize [ON] and [OFF] pixels. The class should be used akin to an enum and was created
 * simply to group the 2 values together.
 *
 * @property ON  The light gray [Color] symbolizing a pixel that is "on".
 * @property OFF The dark  gray [Color] symbolizing a pixel that is "off"
 */
object Pixel {
        val ON  = Color(200, 200, 200)
        val OFF = Color(50, 50, 50)
}
