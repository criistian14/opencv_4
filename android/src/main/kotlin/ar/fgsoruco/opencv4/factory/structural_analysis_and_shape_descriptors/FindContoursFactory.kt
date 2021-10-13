package ar.fgsoruco.opencv4.factory.structural_analysis_and_shape_descriptors

import android.util.Log
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import io.flutter.plugin.common.MethodChannel
import org.opencv.core.*
import java.util.HashMap

class FindContoursFactory {
    companion object {
        fun process(
            pathType: Int,
            pathString: String,
            data: ByteArray,
            mode: Int,
            method: Int,
            offsetX: Double,
            offsetY: Double,
            result: MethodChannel.Result
        ) {
            when (pathType) {
                1 -> result.success(findContoursS(pathString, mode, method, offsetX, offsetY))
                2 -> result.success(findContoursB(data, mode, method, offsetX, offsetY))
                3 -> result.success(findContoursB(data, mode, method, offsetX, offsetY))
            }
        }

        private fun findContoursS(
            pathString: String,
            mode: Int,
            method: Int,
            offsetX: Double,
            offsetY: Double
        ): List<HashMap<String, Double>>? {
            try {
                val filename = pathString.replace("file://", "")

                val contours = mutableListOf<MatOfPoint>()
                val hierarchy = Mat()
                val srcGray = Mat()
                val src = Imgcodecs.imread(filename)

                // Convert the image to Gray
                Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY)

                // Find Contours
                Imgproc.findContours(srcGray, contours, hierarchy, mode, method, Point(offsetX, offsetY))
                hierarchy.release()

                val parsedContours = mutableListOf<HashMap<String, Double>>()
                contours.forEach { contour: MatOfPoint ->
//                    val approxCurve = MatOfPoint2f()
//                    val contour2f = MatOfPoint2f()
//                    contour.convertTo(contour2f, CvType.CV_32FC2)
//
//                    val approxDistance = Imgproc.arcLength(contour2f, true) * 0.01
//                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)
//
//                    val points = MatOfPoint()
//                    approxCurve.convertTo(points, CvType.CV_8UC4)

                    val rectTemp = Imgproc.boundingRect(contour)
                    parsedContours.add(
                        hashMapOf<String, Double>(
                            "x" to rectTemp.x.toDouble(),
                            "y" to rectTemp.y.toDouble(),
                            "width" to rectTemp.width.toDouble(),
                            "height" to rectTemp.height.toDouble()
                        )
                    )
                }

                parsedContours.add(
                    hashMapOf<String, Double>(
                        "x" to contours.size.toDouble(),
                        "y" to 10.2,
                        "width" to 534.3,
                        "height" to 342.12
                    )
                )

                return parsedContours
            } catch (e: Exception) {
                return null
            }
        }

        private fun findContoursB(
            data: ByteArray,
            mode: Int,
            method: Int,
            offsetX: Double,
            offsetY: Double
        ): List<Map<String, Double>>? {
            return try {
                val contours = listOf<MatOfPoint>()
                val hierarchy = Mat()
                val src = Imgcodecs.imdecode(MatOfByte(*data), Imgcodecs.IMREAD_UNCHANGED)
                Imgproc.findContours(src, contours, hierarchy, mode, method, Point(offsetX, offsetY))
                hierarchy.release()

                val parsedContours = mutableListOf<Map<String, Double>>()
                contours.forEach { contour: MatOfPoint ->
                    val approxCurve = MatOfPoint2f()
                    val contour2f = MatOfPoint2f()
                    contour.convertTo(contour2f, CvType.CV_32FC2)

                    val approxDistance = Imgproc.arcLength(contour2f, true) * 0.01
                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

                    val points = MatOfPoint()
                    approxCurve.convertTo(points, CvType.CV_8UC4)

                    val rectTemp = Imgproc.boundingRect(points)
                    parsedContours.add(
                        mapOf<String, Double>(
                            "x" to rectTemp.x.toDouble(),
                            "y" to rectTemp.y.toDouble(),
                            "width" to rectTemp.width.toDouble(),
                            "height" to rectTemp.height.toDouble()
                        )
                    )
                }

                parsedContours
            } catch (e: java.lang.Exception) {
                println("OpenCV Error: $e")
                null
            }
        }
    }
}