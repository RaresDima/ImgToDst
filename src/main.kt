import clustering.supervised.Point
import clustering.supervised.SupervisedKMeans
import dst.writer.DstWritablePointList
import marvin.extensions.Color
import utils.misc.then
import marvin.extensions.Image

object inImgPath {
        val shapes    = "res/shapes.png"
        val face      = "res/face.jpg"
        val face2     = "res/face2.jpg"
        val monastery = "res/monastery.jpg"
        val diamond   = "res/diamond.png"
}

object outImgPath {
        val shapes    = "res/shapes_edit.png"
        val face      = "res/face_edit.jpg"
        val face2     = "res/face2_edit.jpg"
        val monastery = "res/monastery_edit.jpg"
        val diamond   = "res/diamond_edit.png"
}

object dstPath {
        val shapes    = "res/shapes.dst"
        val face      = "res/face.dst"
        val face2     = "res/face2.dst"
        val monastery = "res/monastery.dst"
        val diamond   = "res/diamond.dst"
}

val highlightColors = listOf(Color(0, 0, 100),
        Color(0, 0, 200),

        Color(0, 100, 0),
        Color(0, 100, 100),
        Color(0, 100, 200),

        Color(0, 200, 0),
        Color(0, 200, 100),
        Color(0, 200, 200),

        Color(100, 0, 0),
        Color(100, 0, 100),
        Color(100, 0, 200),

        Color(100, 100, 0),
        Color(100, 100, 200),

        Color(100, 200, 0),
        Color(100, 200, 100),
        Color(100, 200, 200),

        Color(200, 0, 0),
        Color(200, 0, 100),
        Color(200, 0, 200),

        Color(200, 100, 0),
        Color(200, 100, 100),
        Color(200, 100, 200),

        Color(200, 200, 0),
        Color(200, 200, 100)).shuffled()


fun main(args: Array<String>) {
        val img = Image.load(inImgPath.shapes)
                .resizeScaled(240, 240)

        val initialColors: List<Point> = (0 until img.height).flatMap { y -> (0 until img.width).map { x -> img[x, y] then ::Point } }
        val reducedColors: List<Point> = SupervisedKMeans(initialColors, 2).run(verbose = false)

        val clusteredImg = img.mapPixels { px -> reducedColors.minBy { it sqrDistanceTo Point(px) }!!.toColor() }

        clusteredImg
                .apply { computeObjects(keepSmallObjects = false, smallObjectThreshold = 50, verbose = false) }
                .colorObjects(highlightColors)
                .colorBackground(newBgColor = Color(255, 255, 255))
                .save(outImgPath.shapes)

        clusteredImg.objects then ::println

        val dstPoints = clusteredImg.objects.flatMap { it.allPointsSorted() } then ::DstWritablePointList

        dstPoints.plotPointsToFile(dstPath.shapes)


}
