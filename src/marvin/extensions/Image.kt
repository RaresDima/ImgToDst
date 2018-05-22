package marvin.extensions


import clustering.supervised.Point
import marvin.MarvinPluginCollection
import marvin.image.MarvinImage
import marvin.io.MarvinImageIO
import utils.misc.then


/**
 * Class wrapping a [MarvinImage] and implementing new functionality to it.
 *
 * @property width  The width  of the image, in pixels.
 * @property height The height of the image, in pixels.
 */
open class Image internal constructor(private val image: MarvinImage) {
        val width  : Int = image.width
        val height : Int = image.height

        val objects: List<ImageObject> by lazy { computeObjects() }

        /**
         * Factory object for the [Image] class.
         */
        companion object Factory {
                /**
                 * Loads the [Image] at the specified location from the disk.
                 *
                 * @param path The path on the disk where the image file is located.
                 *
                 * @return The [Image] object.
                 */
                fun load(path: String) : Image = Image(MarvinImageIO.loadImage(path))
        }

        /**
         * Saves the current [Image] on the disk and returns the current [Image] for convenience.
         *
         * @param path Where on the disk should the [Image] be saved.
         *
         * @return The current [Image] so further methods can be chained.
         */
        fun save(path: String) : Image = MarvinImageIO.saveImage(image, path).let { this }

        /**
         * Clones the current [Image], returning a copy of it.
         *
         * @return A copy of the current [Image].
         */
        fun clone() : Image = Image(image.clone())


        /**
         * The [Image] [[x], [y]] operator. Returns the [Color] of the pixel at position ([x],[y]).
         *
         * @param x The [x] coordinate of the pixel.
         * @param y The [y] coordinate of the pixel.
         *
         * @return The [Color] of the specified pixel.
         */
        operator fun get(x: Int, y: Int) : Color = Color(image.getIntComponent0(x, y), image.getIntComponent1(x, y), image.getIntComponent2(x, y))

        /**
         * The [Image] [[pos]] operator. Returns the [Color] of the pixel at the position described by [pos].
         *
         * @param pos The position of the specified pixel
         *
         * @return The [Color] of the specified pixel.
         */
        operator fun get(pos: Point) : Color = Color(image.getIntComponent0(pos[0], pos[1]), image.getIntComponent1(pos[0], pos[1]), image.getIntComponent2(pos[0], pos[1]))


        /**
         * The [Image] [[x], [y]] = [Color] operator. Sets the pixel at position ([x],[y]) to the specified [Color].
         *
         * @param x     The [x] coordinate of the pixel.
         * @param y     The [y] coordinate of the pixel.
         * @param color The [Color] the pixel should be set to.
         */
        operator fun set(x: Int, y: Int, color: Color) = image.setIntColor(x, y, color.red, color.green, color.blue)

        /**
         * The [Image] [[pos]] = [Color] operator. Sets the pixel at position [pos] to the specified [Color].
         *
         * @param pos   The position at which to set the pixel value.
         * @param color The [Color] the pixel should be set to.
         */
        operator fun set(pos: Point, color: Color) = image.setIntColor(pos[0], pos[1], color.red, color.green, color.blue)


        /**
         * Maps the [Color] of every pixel in the current [Image] trough a function.
         *
         * @param f The function each pixel should be mapped to.
         *
         * @return An [Image] with the pixels mapped trough the specified function.
         */
        fun mapPixels(f: (Color) -> Color) : Image {
                val result = clone()
                for (x in 0 until width)
                        for (y in 0 until height)
                                result[x, y] = f(this[x, y])
                return result
        }

        /**
         * Resizes the [Image] to have its dimensions at most [x] and [y] while preserving the aspect ratio.
         *
         * If either [x] or [y] is not given, the [Image] is not resized along that dimension. Omitting both parameters
         * will return a copy of the current [Image].
         *
         * @param x (Optional) The [x] dimensions to which the image should be resized.
         * @param y (Optional) The [y] dimensions to which the image should be resized.
         *
         * @return A resized copy of the current [Image].
         */
        fun resizeScaled(x: Int = -1, y: Int = -1) : Image {
                val result = image.clone()
                if (x != -1)
                        MarvinPluginCollection.thumbnailByHeight(result.clone(), result, x)
                if (y != -1)
                        MarvinPluginCollection.thumbnailByWidth(result.clone(), result, y)
                return Image(result)
        }


        /**
         * Computes and returns the list of all the [ImageObject]s in the current [Image].
         *
         * Visits all the pixels in the current [Image] and flood-visits any area that is not of color [bgColor].
         *
         * @param bgColor              (Optional) The [Color] to be used as the background color for the object
         *                                        detection. Defaults to the color of the pixel in the upper left corner.
         * @param verbose              (Optional) Whether or not to print intermediate information.
         * @param keepSmallObjects     (Optional) Whether to eliminate small objects by coloring them using the same
         *                                        color as the background color.
         * @param smallObjectThreshold (Optional) The minimum number of pixels an [ImageObject] should contain not to be
         *                                        considered a small object.
         *
         * @return The [List] of all [ImageObject]s found in this [Image].
         */
        fun computeObjects(bgColor: Color = this[0, 0], verbose: Boolean = false,
                           keepSmallObjects: Boolean = true, smallObjectThreshold: Int = 50) : List<ImageObject> {

                val mutObjects  : MutableList<ImageObject>          = mutableListOf()

                val markedPixels: MutableList<MutableList<Boolean>> = MutableList(width) { MutableList(height) { false } }

                fun Point.mark()               { markedPixels[this[0]][this[1]] = true }
                fun Point.isMarked() : Boolean = markedPixels[this[0]][this[1]]

                for (x in 0 until width) {
                        for (y in 0 until height) {

                                val currPoint = Point(x, y)

                                if (currPoint.isMarked()) continue else currPoint.mark()

                                if (this[currPoint] eq bgColor) continue

                                val newObject = ImageObject(this, Point(x, y), verbose = verbose, objIndex = mutObjects.count())
                                newObject.allPoints().forEach { it.mark() }

                                if (keepSmallObjects or (newObject.area() >= smallObjectThreshold))
                                        mutObjects.add(newObject)
                                else
                                        newObject.allPoints().forEach { this[it] = bgColor }
                        }
                }

                return mutObjects.toList()
        }

        /**
         * Returns a copy of this [Image] with the pixels belonging to [imageObject] colored to [color].
         *
         * @param imageObject            The [ImageObject] containing the [Point]s to be colored.
         * @param color       (Optional) The [Color] to change the value of the pixels to. Defaults to a bright green.
         *
         * @return A copy of this [Image] with the pixels belonging to [imageObject] colored to [color].
         */
        fun colorObject(imageObject: ImageObject, color: Color = Color(50, 250, 50)) : Image =
                clone().also { img -> imageObject.allPoints().forEach { img[it] = color } }

        /**
         * Returns this [Image] with the pixels belonging to [imageObject] colored to [color].
         *
         * @param imageObject            The [ImageObject] containing the [Point]s to be colored.
         * @param color       (Optional) The [Color] to change the value of the pixels to. Defaults to a bright green.
         *
         * @return This [Image] with the pixels belonging to [imageObject] colored to [color], in-place.
         */
        private fun inPlaceColorObject(imageObject: ImageObject, color: Color = Color(50, 250, 50)) : Image =
                apply { imageObject.allPoints().forEach { this[it] = color } }


        /**
         * Returns this [Image] with every [ImageObject] colored.
         *
         * Cycles the [colors] [List] and colors each [ImageObject] of this [Image] using the next [Color].
         *
         * @param colors The [List] or [Color]s to be used.
         *
         * @return A copy of this [Image] with all its [ImageObject]s colored.
         */
        fun colorObjects(colors: List<Color>) : Image {
                for (objInd in objects.indices)
                        inPlaceColorObject(objects[objInd], colors[objInd % colors.count()])
                return this
        }


        /**
         * Returns this [Image] with its background color changed to [newBgColor].
         *
         * Any pixels having the color described by [oldBgColor] will be changed to [newBgColor].
         *
         * @param newBgColor (Optional) The [Color] to be used for the new background. Defaults to pure white.
         * @param oldBgColor (Optional) The [Color] to be considered as the old background. Defaults to the color of the
         *                   pixel in the upper left corner.
         *
         * @return This [Image] with the changed background.
         */
        fun colorBackground(newBgColor: Color = Color(0, 0, 0), oldBgColor: Color = this[0, 0]) : Image = apply {
                for (x in 0 until width)
                        for (y in 0 until height)
                                if (this[x, y] eq oldBgColor)
                                        this[x, y] = newBgColor
        }

}