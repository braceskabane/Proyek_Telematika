package com.dicoding.hanebado.view.dashboard.record

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain
import com.dicoding.hanebado.databinding.ActivityRecordBinding
import com.dicoding.hanebado.view.dashboard.record.dialogplan.ShowPlanDialog
import com.dicoding.hanebado.view.dashboard.record.ml.OverlayView
import com.dicoding.hanebado.view.dashboard.record.ml.PoseLandmarkerHelper
import com.dicoding.hanebado.view.dashboard.record.ml.PoseLandmarkerHelper.Companion.MODEL_POSE_LANDMARKER_FULL
import com.google.mediapipe.tasks.vision.core.RunningMode
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class RecordActivity : AppCompatActivity(), PoseLandmarkerHelper.LandmarkerListener {
    private lateinit var binding: ActivityRecordBinding
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private lateinit var backgroundExecutor: ExecutorService
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var overlayView: OverlayView
    private var selectedExercise: TodayExerciseDomain? = null
    private var seconds = 0
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var isDialogShown = false

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                seconds++
                updateTimerUI()
                handler.postDelayed(this, 1000) // Update setiap detik
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isDialogShown) {
            showPlanDialog()
        }

        // Inisialisasi overlayView
        overlayView = findViewById(R.id.overlay_view)

        // Initialize background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Initialize PoseLandmarkerHelper
        backgroundExecutor.execute {
            poseLandmarkerHelper = PoseLandmarkerHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                minPoseDetectionConfidence = PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE,
                minPoseTrackingConfidence = PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE,
                minPosePresenceConfidence = PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE,
                currentDelegate = PoseLandmarkerHelper.DELEGATE_CPU,
                poseLandmarkerHelperListener = this,
                currentModel = MODEL_POSE_LANDMARKER_FULL
            )
        }


        setupPlankStatusView()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun showPlanDialog() {
        val dialog = ShowPlanDialog().apply {
            exerciseSelectedListener = object : OnTodayExerciseSelectedListener {
                override fun onExerciseSelected(exercise: TodayExerciseDomain) {
                    selectedExercise = exercise
                    setupExerciseUI(exercise)
                    initializeCameraSetup()
                }
            }
        }
        dialog.onDismissListener = {
            isDialogShown = true // Tandai bahwa dialog sudah ditampilkan
        }
        dialog.show(supportFragmentManager, ShowPlanDialog.TAG)
    }


    private fun setupExerciseUI(exercise: TodayExerciseDomain) {
        binding.apply {
            tvExerciseName.text = exercise.exercise.name
            tvTimer.visibility = View.VISIBLE
            tvTimer.text = "00:00:00"
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable)
        }
    }

    private fun stopTimer() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimerUI() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        binding.tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        runOnUiThread {
            // Update overlay view dengan pose landmarks
            overlayView.setResults(
                resultBundle.results.first(),
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            // Force a redraw
            overlayView.invalidate()
        }
    }



    override fun onPoseResult(poseLabel: String, poseConfidence: Float) {
        runOnUiThread {
            binding.apply {
                tvPlankStatus.text = poseLabel
                tvPlankStatus.setTextColor(
                    ContextCompat.getColor(
                        this@RecordActivity,
                        when (poseLabel) {
                            "Correct" -> R.color.green
                            "High Back" -> R.color.orange_100
                            "Low Back" -> R.color.red_100
                            else -> R.color.grey_navbar
                        }
                    )
                )

                // Tampilkan probabilitas
                tvPlankConfidence.text = String.format("Confidence: %.1f%%", poseConfidence * 100)

                // Panduan berdasarkan pose
                tvPlankGuide.text = when (poseLabel) {
                    "Correct" -> "Great form! Maintain this position"
                    "High Back" -> "Lower your back"
                    "Low Back" -> "Raise your back"
                    "Uncertain" -> "Please position yourself correctly"
                    else -> "Adjusting..."
                }
            }
        }
    }

    private fun initializeCameraSetup() {
        // Pindahkan inisialisasi kamera dll ke sini
        overlayView = findViewById(R.id.overlay_view)
        backgroundExecutor = Executors.newSingleThreadExecutor()

        backgroundExecutor.execute {
            poseLandmarkerHelper = PoseLandmarkerHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                minPoseDetectionConfidence = PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE,
                minPoseTrackingConfidence = PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE,
                minPosePresenceConfidence = PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE,
                currentDelegate = PoseLandmarkerHelper.DELEGATE_CPU,
                poseLandmarkerHelperListener = this,
                currentModel = MODEL_POSE_LANDMARKER_FULL
            )
        }

        setupPlankStatusView()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // Preview use case
            preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // ImageAnalysis use case
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        detectPose(image)
                    }
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build()

            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun detectPose(imageProxy: ImageProxy) {
        poseLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        )
    }

    private fun setupPlankStatusView() {
        binding.apply {
            statusCard.apply {
                visibility = View.VISIBLE
                elevation = 8f
                radius = 16f
            }
            tvPlankStatus.apply {
                visibility = View.VISIBLE
                text = "Preparing Camera..."
            }
            tvPlankConfidence.apply {
                visibility = View.VISIBLE
                text = "Position yourself in frame"
            }
            tvPlankGuide.apply {
                visibility = View.VISIBLE
                text = "Waiting for pose detection..."
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            if (errorCode == PoseLandmarkerHelper.GPU_ERROR) {
                // Handle GPU error if needed
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun onDoneButtonClicked() {
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        // Restart pose detection if needed
        backgroundExecutor.execute {
            if (poseLandmarkerHelper.isClose()) {
                poseLandmarkerHelper.setupPoseLandmarker()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        backgroundExecutor.execute {
            poseLandmarkerHelper.clearPoseLandmarker()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        backgroundExecutor.shutdown()
    }

    companion object {
        private const val TAG = "RecordActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}