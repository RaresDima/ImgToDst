package ui

import dst.writer.DstWritablePointList
import clustering.supervised.Point
import clustering.supervised.SupervisedKMeans
import debug.dstPath
import debug.highlightColors
import debug.inImgPath
import debug.outImgPath
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import marvin.extensions.Color
import marvin.extensions.Image
import tornadofx.*
import utils.misc.then
import java.io.File
import kotlin.system.exitProcess

class MainView : View("My View") {
        override val root: BorderPane by fxml()

        val checkBox_enableNoiseThreshold by fxid<CheckBox> ("checkBox_enableNoiseThreshold")
        val textBox_noiseThreshold        by fxid<TextField>("textBox_noiseThreshold")

        val button_exit                   by fxid<Button>   ("button_exit")
        val button_convert                by fxid<Button>   ("button_convert")

        val button_browseInputImage       by fxid<Button>   ("button_browseInputImage")
        val button_browseOutputFolder     by fxid<Button>   ("button_browseOutputFolder")

        val textBox_inputImagePath        by fxid<TextField>("textBox_inputImage")
        val textBox_outputFolderPath      by fxid<TextField>("textBox_outputFolder")

        val image_preCluster              by fxid<ImageView>("image_preCluster")
        val image_postCluster             by fxid<ImageView>("image_postCluster")


        init {
                checkBox_enableNoiseThreshold.tooltip = Tooltip().apply {
                        text = """
                                | Very small distinct areas in the image(such as grains of sand) can reduce embroidery quality.
                                | This setting enables manually selecting how small(in pixels) should an object in the image be
                                | before it is considered "noise" and removed from the resulting embroidery.
                                """.trimMargin()

                }

                primaryStage.isResizable = false
        }


        fun onChangeEnableNoiseThreshold() { textBox_noiseThreshold.isDisable = ! checkBox_enableNoiseThreshold.isSelected }


        fun onClickBrowseInputImage() =
                with(FileChooser().apply { setTitle("Output Folder") }.showOpenDialog(primaryStage)) {
                        if (this != null)
                                textBox_inputImagePath.text = this.toString()
                }



        fun onClickBrowseOutputFolder() =
                with(DirectoryChooser().apply { setTitle("Output Folder") }.showDialog(primaryStage)) {
                        if (this != null)
                                textBox_outputFolderPath.text = this.toString()
                }


        fun onExit() { exitProcess(0) }


        fun onConvert() {
                button_exit.isDisable               = true
                button_convert.isDisable            = true
                button_browseInputImage.isDisable   = true
                button_browseOutputFolder.isDisable = true

                button_convert.text = "Checking threshold..."

                try {
                        if (checkBox_enableNoiseThreshold.isSelected)
                                if (textBox_inputImagePath.text.toInt() < 1)
                                        throw java.lang.NumberFormatException()
                }
                catch (e: Exception) {
                        Alert(Alert.AlertType.ERROR).apply {
                                setTitle("Invalid Threshold");
                                setContentText("The threshold field is not a positive integer higher than 1.")
                        }.showAndWait()
                        button_exit.isDisable               = false
                        button_convert.isDisable            = false
                        button_browseInputImage.isDisable   = false
                        button_browseOutputFolder.isDisable = false
                        return
                }

                val inputImage = File(textBox_inputImagePath.text)
                val outputFolder = File(textBox_outputFolderPath.text)

                button_convert.text = "Checking input image..."

                if(!inputImage.exists()) {
                        Alert(Alert.AlertType.ERROR).apply {
                                setTitle("Invalid Input Image");
                                setContentText("There is no file with the path given as the input image path.")
                        }.showAndWait()
                        button_exit.isDisable               = false
                        button_convert.isDisable            = false
                        button_browseInputImage.isDisable   = false
                        button_browseOutputFolder.isDisable = false
                        return
                }

                if(inputImage.isDirectory) {
                        Alert(Alert.AlertType.ERROR).apply {
                                setTitle("Invalid Input Image");
                                setContentText("The path given as the input image path is a directory.")
                        }.showAndWait()
                        button_exit.isDisable               = false
                        button_convert.isDisable            = false
                        button_browseInputImage.isDisable   = false
                        button_browseOutputFolder.isDisable = false
                        return
                }

                image_preCluster.image = javafx.scene.image.Image(inputImage.toURI().toString())

                button_convert.text = "Checking output folder..."

                if(!outputFolder.exists()) {
                        Alert(Alert.AlertType.ERROR).apply {
                                setTitle("Invalid Output Folder");
                                setContentText("There is no folder with the path given as the output folder path.")
                        }.showAndWait()
                        button_exit.isDisable               = false
                        button_convert.isDisable            = false
                        button_browseInputImage.isDisable   = false
                        button_browseOutputFolder.isDisable = false
                        return
                }

                if(!outputFolder.isDirectory) {
                        Alert(Alert.AlertType.ERROR).apply {
                                setTitle("Invalid Output Folder");
                                setContentText("The path given as the output folder path is a directory.")
                        }.showAndWait()
                        button_exit.isDisable               = false
                        button_convert.isDisable            = false
                        button_browseInputImage.isDisable   = false
                        button_browseOutputFolder.isDisable = false
                        return
                }

                button_convert.text = "Clustering image..."

                val img = Image.load(inputImage.path).resizeScaled(240, 240)

                val initialColors: List<Point> = (0 until img.height).flatMap { y -> (0 until img.width).map { x -> img[x, y] then ::Point } }
                val reducedColors: List<Point> = SupervisedKMeans(initialColors, 2).run(verbose = false)

                val clusteredImg = img.mapPixels { px -> reducedColors.minBy { it sqrDistanceTo Point(px) }!!.toColor() }

                clusteredImg
                        .apply {
                                computeObjects(
                                        keepSmallObjects = checkBox_enableNoiseThreshold.isSelected,
                                        smallObjectThreshold =
                                                when (checkBox_enableNoiseThreshold.isSelected) {
                                                        true  -> textBox_noiseThreshold.text.toInt()
                                                        false -> 0
                                                },
                                        verbose = false)
                        }
                        .colorObjects(listOf(Color(50, 50, 50)))
                        .colorBackground(newBgColor = Color(250, 250, 250))
                        .save("${outputFolder.path}\\${inputImage.nameWithoutExtension}_edit.${inputImage.extension}")

                image_postCluster.image = javafx.scene.image.Image(File("${outputFolder.path}\\${inputImage.nameWithoutExtension}_edit.${inputImage.extension}").toURI().toString())

                button_convert.text = "Creating DST file..."

                val dstPoints = clusteredImg.objects.flatMap { it.allPointsSorted() } then ::DstWritablePointList

                dstPoints.plotPointsToFile("${outputFolder.path}\\${inputImage.nameWithoutExtension}_embroidery.dst")

                Alert(Alert.AlertType.INFORMATION).apply { setContentText("Conversion complete.") }.show()

                button_exit.isDisable               = false
                button_convert.isDisable            = false
                button_browseInputImage.isDisable   = false
                button_browseOutputFolder.isDisable = false
        }
}
