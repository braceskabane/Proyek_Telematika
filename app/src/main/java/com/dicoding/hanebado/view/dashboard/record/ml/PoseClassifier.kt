package com.dicoding.hanebado.view.dashboard.record.ml

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PoseClassifier(context: Context, modelPath: String) {
    var confidence: Float = 0.0f
    private var tflite: Interpreter
    private val mean: FloatArray
    private val std: FloatArray
    private val INPUT_SIZE = 68  // Sesuaikan dengan jumlah landmark (misalnya, 68)

    init {
        // Memuat model TFLite
        val tfliteModel = loadModelFile(context, modelPath)
        tflite = Interpreter(tfliteModel)

        // Memuat mean dan std dari JSON
        val scalerData = loadScalerData(context)
        mean = scalerData.first
        std = scalerData.second
    }

    // Fungsi untuk memuat file model TFLite
    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Fungsi untuk memuat mean dan std dari JSON di assets
    private fun loadScalerData(context: Context): Pair<FloatArray, FloatArray> {
        val json: String = context.assets.open("scaler_data.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)

        val meanArray = jsonObject.getJSONArray("mean")
        val scaleArray = jsonObject.getJSONArray("scale")

        val mean = FloatArray(meanArray.length()) { meanArray.getDouble(it).toFloat() }
        val std = FloatArray(scaleArray.length()) { scaleArray.getDouble(it).toFloat() }

        return Pair(mean, std)
    }

    fun classifyPose(landmarks: FloatArray): String {
        // Log untuk debugging
        Log.d("PoseClassifier", "Ukuran landmarks: ${landmarks.size}, Mean size: ${mean.size}, Std size: ${std.size}")

        // Pastikan ukuran landmarks sesuai dengan mean dan std
        if (landmarks.size != mean.size || landmarks.size != std.size) {
            Log.e("PoseClassifier", "Ukuran landmarks tidak sesuai dengan mean/std")
            return "Error: Size mismatch"
        }

        // Normalisasi input dengan mean dan std scaler
        val normalizedLandmarks = FloatArray(landmarks.size)
        for (i in landmarks.indices) {
            normalizedLandmarks[i] = (landmarks[i] - mean[i]) / std[i]
        }

        // Buat buffer input
        val inputBuffer = ByteBuffer.allocateDirect(normalizedLandmarks.size * 4).order(ByteOrder.nativeOrder())
        for (value in normalizedLandmarks) {
            inputBuffer.putFloat(value)
        }

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 3), org.tensorflow.lite.DataType.FLOAT32)
        tflite.run(inputBuffer, outputBuffer.buffer.rewind())

        val probabilities = outputBuffer.floatArray
        Log.d("PoseClassifier", "Probabilitas: ${probabilities.joinToString()}")
        val predictedClass = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        confidence = probabilities[predictedClass]

       return if (confidence >= 0.6) {
            when (predictedClass) {
                0 -> "Correct"
                1 -> "High Back"
                2 -> "Low Back"
                else -> "Unknown"
            }
        } else {
            return "Uncertain"
        }
    }

}
