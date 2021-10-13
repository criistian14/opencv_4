package ar.fgsoruco.opencv4.factory.detection

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.FileInputStream
import java.io.InputStream
import io.flutter.plugin.common.MethodChannel

class CannyFactory {
    companion object {
        fun process(
            pathType: Int,
            pathString: String,
            data: ByteArray,
            threshold1: Double,
            threshold2: Double,
            result: MethodChannel.Result
        ) {
            when (pathType) {
                1 -> result.success(cannyS(pathString, threshold1, threshold2))
                2 -> result.success(cannyB(data, threshold1, threshold2))
                3 -> result.success(cannyB(data, threshold1, threshold2))
            }
        }

        private fun cannyS(
            pathString: String,
            threshold1: Double,
            threshold2: Double
        ): ByteArray? {
            val inputStream: InputStream = FileInputStream(pathString.replace("file://", ""))
            val data: ByteArray = inputStream.readBytes()
            return try {
                var byteArray = ByteArray(0)
                val dst = Mat()
                val filename = pathString.replace("file://", "")
                val src = Imgcodecs.imread(filename)
                Imgproc.Canny(src, dst, threshold1, threshold2)
                val matOfByte = MatOfByte()
                Imgcodecs.imencode(".jpg", dst, matOfByte)
                byteArray = matOfByte.toArray()
                byteArray
            } catch (e: Exception) {
                data
            }
        }

        private fun cannyB(
            data: ByteArray,
            threshold1: Double,
            threshold2: Double
        ): ByteArray? {
            return try {
                var byteArray = ByteArray(0)
                val dst = Mat()
                val src = Imgcodecs.imdecode(MatOfByte(*data), Imgcodecs.IMREAD_UNCHANGED)
                Imgproc.Canny(src, dst, threshold1, threshold2)
                val matOfByte = MatOfByte()
                Imgcodecs.imencode(".jpg", dst, matOfByte)
                byteArray = matOfByte.toArray()
                byteArray
            } catch (e: java.lang.Exception) {
                println("OpenCV Error: $e")
                data
            }
        }
    }
}