package com.dicoding.hanebado.view.dashboard.record

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.data.source.local.entity.plan.Plan
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain
import com.dicoding.hanebado.databinding.ActivityRecordBinding
import com.dicoding.hanebado.view.dashboard.record.dialogplan.ShowPlanDialog
import com.dicoding.hanebado.view.dashboard.record.dialogrest.RestDialog
import com.dicoding.hanebado.view.dashboard.record.graphic.GraphicOverlay
import com.dicoding.hanebado.view.dashboard.record.preference.PreferenceUtils
import com.dicoding.hanebado.view.dashboard.record.util.VisionImageProcessor
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private lateinit var cameraXViewModel: CameraXViewModel
    private lateinit var backgroundExecutor: ExecutorService
    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null

    private var selectedModel = POSE_DETECTION
    private var selectedExercise: TodayExerciseDomain? = null
    private var isExerciseSelected = false

    private var restTimer: CountDownTimer? = null

    private var isRestDialogShowing = false
    private var isWorkoutInProgress = false
    private var currentSetNumber = 1

    // Timer variables
    private var mRecTimer: Timer? = null
    private var mRecSeconds = 0
    private var mRecMinute = 0
    private var mRecHours = 0
    private val mMainHandler: Handler by lazy {
        Handler(Looper.getMainLooper()) {
            when (it.what) {
                WHAT_START_TIMER -> {
                    binding.tvTimer.text = calculateTime(mRecSeconds, mRecMinute, mRecHours)
                }
                WHAT_STOP_TIMER -> {
                    binding.tvTimer.text = calculateTime(0, 0)
                    binding.tvTimer.visibility = View.GONE
                }
            }
            true
        }
    }

    private val onlyExercise: List<String> = listOf(
        PUSHUPS_CLASS,
        SQUATS_CLASS,
        LUNGES_CLASS,
        SITUP_UP_CLASS,
        CHEST_PRESS_CLASS,
        DEAD_LIFT_CLASS,
        SHOULDER_PRESS_CLASS
    )

    private fun mapExerciseNameToClass(exerciseName: String): String? {
        Log.d(TAG, "Mapping exercise name: $exerciseName")
        return when (exerciseName) {
            "Push-up" -> PUSHUPS_CLASS
            "Squat" -> SQUATS_CLASS
            "Lunges" -> LUNGES_CLASS
            "Sit-up" -> SITUP_UP_CLASS
            "Chest press" -> CHEST_PRESS_CLASS
            "Dead lift" -> DEAD_LIFT_CLASS
            "Shoulder press" -> SHOULDER_PRESS_CLASS
            else -> null
        }.also {
            Log.d(TAG, "Mapped exercise '$exerciseName' to class name: $it")
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
        setupObservers()

        val exerciseId = intent.getStringExtra("exerciseId")
        Log.d(TAG, "Received exerciseId: $exerciseId")

        if (exerciseId == null && !isExerciseSelected) {
            showPlanDialog()
        } else {
            setupInitialState()
        }
    }

    private fun initializeComponents() {
        cameraXViewModel = ViewModelProvider(this)[CameraXViewModel::class.java]
        backgroundExecutor = Executors.newSingleThreadExecutor()

        previewView = binding.previewView
        graphicOverlay = binding.graphicOverlay
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    private fun showPlanDialog() {
        if (supportFragmentManager.findFragmentByTag(ShowPlanDialog.TAG) != null) return

        val dialog = ShowPlanDialog().apply {
            exerciseSelectedListener = object : OnTodayExerciseSelectedListener {
                override fun onExerciseSelected(exercise: TodayExerciseDomain) {
                    handleExerciseSelection(exercise)
                }
            }
        }
        dialog.show(supportFragmentManager, ShowPlanDialog.TAG)
    }

    private fun setupExerciseUI(exercise: TodayExerciseDomain) {
        binding.apply {
            tvExerciseName.text = exercise.exercise.name
            // Gunakan current set yang sudah ada, jika belum ada baru set ke 1
            tvSetsNumber.text = currentSetNumber.toString()
            tvRepsNumber.text = "0"
            tvWorkoutStatus.text = "Ready"
            tvWorkoutConfidence.text = "Position yourself correctly"
            tvWorkoutGuide.text = "Waiting for pose detection..."

            startMediaTimer()
            tvTimer.visibility = View.VISIBLE
        }
    }

    private fun setupInitialState() {
        Log.d(TAG, "Setting up initial state")

        // Check if exercise is supported first
        selectedExercise?.let { exercise ->
            val supportedExercises = listOf("Push-up", "Squat", "Sit-up", "Lunges")

            if (!supportedExercises.contains(exercise.exercise.name)) {
                Toast.makeText(
                    this,
                    "Sorry, ${exercise.exercise.name} detection is not available yet",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return
            }

            Log.d(TAG, "Starting supported exercise: ${exercise.exercise.name}")

            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                try {
                    cameraProvider = cameraProviderFuture.get()

                    if (allPermissionsGranted()) {
                        Log.d(TAG, "Permissions granted, binding camera cases")
                        bindAllCameraUseCases()
                    } else {
                        Log.d(TAG, "Requesting permissions")
                        ActivityCompat.requestPermissions(
                            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                        )
                    }

                    // Start pose detection after camera is initialized
                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.d(TAG, "Starting pose detection for ${exercise.exercise.name}")
                        cameraXViewModel.triggerClassification.value = true
                    }, 1000)

                } catch (e: Exception) {
                    Log.e(TAG, "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(this))
        } ?: run {
            Log.e(TAG, "No exercise selected")
            Toast.makeText(
                this,
                "No exercise selected",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            isWorkoutInProgress = true // Set true saat workout dimulai
            cameraXViewModel.triggerClassification.value = true
        }, 1000)
    }

    private fun bindAllCameraUseCases() {
        bindPreviewUseCase()
        bindAnalysisUseCase(true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                bindAllCameraUseCases()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }

        if (cameraProvider == null) {
            return
        }

        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }

        previewUseCase = builder.build().also {
            it.setSurfaceProvider(previewView!!.surfaceProvider)
        }

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    private fun bindAnalysisUseCase(runClassification: Boolean) {
        selectedExercise?.let { exercise ->
            if (cameraProvider == null) {
                Log.d(TAG, "Camera provider is null")
                return
            }

            try {
                // Convert exercise type to proper format
                val exerciseName = mapExerciseNameToClass(exercise.exercise.name)
                if (exerciseName == null) {
                    Log.e(TAG, "Invalid exercise type: ${exercise.exercise.name}")
                    return
                }

                imageProcessor = when (selectedModel) {
                    POSE_DETECTION -> {
                        val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
                        val shouldShowInFrameLikelihood = PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
                        val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
                        val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)

                        val plan = Plan(
                            id = exercise.id,
                            exercise = exercise.exercise.name,
                            repeatCount = exercise.reps,
                            completed = exercise.isCompleted,
                        )

                        PoseDetectorProcessor(
                            context = this,
                            options = poseDetectorOptions,
                            showInFrameLikelihood = shouldShowInFrameLikelihood,
                            visualizeZ = visualizeZ,
                            rescaleZForVisualization = rescaleZ,
                            runClassification = runClassification,
                            isStreamMode = true,
                            cameraXViewModel = cameraXViewModel,
                            notCompletedExercise = listOf(plan)
                        )
                    }
                    else -> throw IllegalStateException("Invalid model name")
                }

                val builder = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)

                analysisUseCase = builder.build()
                needUpdateGraphicOverlayImageSourceInfo = true

                analysisUseCase?.setAnalyzer(
                    ContextCompat.getMainExecutor(this)
                ) { imageProxy: ImageProxy ->
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            graphicOverlay!!.setImageSourceInfo(
                                imageProxy.width,
                                imageProxy.height,
                                isImageFlipped
                            )
                        } else {
                            graphicOverlay!!.setImageSourceInfo(
                                imageProxy.height,
                                imageProxy.width,
                                isImageFlipped
                            )
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false
                    }

                    try {
                        imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
                    } catch (e: MlKitException) {
                        Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector!!,
                    analysisUseCase
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up image analysis", e)
                e.printStackTrace()
            }
        }
    }

    private fun setupObservers() {
        cameraXViewModel.postureLiveData.observe(this) { postureResults ->
            if (isRestDialogShowing || !isWorkoutInProgress || supportFragmentManager.findFragmentByTag(
                    RestDialog.TAG
                ) != null || binding.tvRepsNumber.text.toString().toInt() >= (selectedExercise?.reps
                    ?: 0)
            ) return@observe


            Log.d(TAG, "Received posture results: $postureResults")

            selectedExercise?.let { exercise ->
                val exerciseClass = when(exercise.exercise.name) {
                    "Push-up" -> "pushups_down"
                    "Squat" -> "squats"
                    "Lunges" -> "lunges"
                    "Sit-up" -> "situp_up"
                    else -> null
                }
                Log.d(TAG, "Looking for exercise: ${exercise.exercise.name} (class: $exerciseClass)")

                if (exerciseClass != null) {
                    postureResults[exerciseClass]?.let { result ->
                        Log.d(TAG, "Found result for $exerciseClass: $result")
                        binding.apply {
                            tvRepsNumber.text = result.repetition.toString()
                            tvWorkoutConfidence.text = String.format("Confidence: %.1f%%", result.confidence * 100)

                            if (result.repetition == exercise.reps) {
                                // Nonaktifkan observer terlebih dahulu
                                isWorkoutInProgress = false

                                // Disable pose detection sementara
                                cameraXViewModel.triggerClassification.value = false

                                val currentSet = tvSetsNumber.text.toString().toInt()

                                if (currentSet >= exercise.sets) {
                                    // Exercise selesai karena set sudah terpenuhi

                                    // TODO: Kirim data hasil exercise ke ViewModel
                                    // Rekomendasi: Buat fungsi di ViewModel seperti
                                    // fun saveExerciseResult(
                                    //     exerciseName: String,
                                    //     totalSets: Int,
                                    //     totalReps: Int,
                                    //     duration: String
                                    // )

//                                    handleExerciseCompletion()
                                    // Reset UI dan tampilkan dialog exercise berikutnya
                                    showPlanDialog()
                                } else {
                                    if (!isRestDialogShowing && supportFragmentManager.findFragmentByTag(RestDialog.TAG) == null) {
                                        isRestDialogShowing = true
                                        showRestDialog(currentSet + 1, exercise)
                                    }
                                }
                            }

                            // Update status based on confidence
                            when {
                                result.confidence > 0.8f -> {
                                    tvWorkoutStatus.text = "Excellent Form!"
                                    tvWorkoutStatus.setTextColor(ContextCompat.getColor(this@RecordActivity, R.color.green))
                                    tvWorkoutGuide.text = "Keep going!"
                                }
                                result.confidence > 0.6f -> {
                                    tvWorkoutStatus.text = "Good Form"
                                    tvWorkoutStatus.setTextColor(ContextCompat.getColor(this@RecordActivity, R.color.reflex))
                                    tvWorkoutGuide.text = "Try to maintain better form"
                                }
                                else -> {
                                    tvWorkoutStatus.text = "Incorrect Form"
                                    tvWorkoutStatus.setTextColor(ContextCompat.getColor(this@RecordActivity, R.color.red_100))
                                    tvWorkoutGuide.text = "Please correct your form"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showRestDialog(nextSet: Int, exercise: TodayExerciseDomain) {
        val dialog = RestDialog(
            nextSet = nextSet,
            onContinueClicked = {
                isRestDialogShowing = false

                currentSetNumber = nextSet

                Toast.makeText(
                    this,
                    "Anda hebat! 1 set gerakan ${exercise.exercise.name} telah dilakukan",
                    Toast.LENGTH_SHORT
                ).show()

                binding.apply {
                    tvSetsNumber.text = currentSetNumber.toString()
                    tvRepsNumber.text = "0"
                    tvWorkoutStatus.text = "Ready"
                    tvWorkoutConfidence.text = "Position yourself correctly"
                    tvWorkoutGuide.text = "Waiting for pose detection..."
                }

                startMediaTimer()

                isWorkoutInProgress = true
                cameraXViewModel.triggerClassification.value = true
            }
        )

        mRecTimer?.cancel()
        dialog.show(supportFragmentManager, RestDialog.TAG)
    }

    private fun handleExerciseSelection(exercise: TodayExerciseDomain) {
        Log.d(TAG, "Handling exercise selection: ${exercise.exercise.name}")
        val exerciseClass = mapExerciseNameToClass(exercise.exercise.name)

        if (exerciseClass != null && exerciseClass in onlyExercise) {
            Log.d(TAG, "Valid exercise selected: $exerciseClass")
            isExerciseSelected = true
            selectedExercise = exercise

            setupExerciseUI(exercise)

            // Reset semua state
            isRestDialogShowing = false
            isWorkoutInProgress = false
            setupInitialState()
        } else {
            Log.d(TAG, "Invalid exercise: ${exercise.exercise.name}")
            Toast.makeText(
                this,
                "Sorry, ${exercise.exercise.name} exercise cannot be detected yet",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun startMediaTimer() {
        val pushTask: TimerTask = object : TimerTask() {
            override fun run() {
                mRecSeconds++
                if (mRecSeconds >= 60) {
                    mRecSeconds = 0
                    mRecMinute++
                }
                if (mRecMinute >= 60) {
                    mRecMinute = 0
                    mRecHours++
                }
                mMainHandler.sendEmptyMessage(WHAT_START_TIMER)
            }
        }
        mRecTimer?.cancel()
        mRecTimer = Timer()
        mRecTimer?.schedule(pushTask, 1000, 1000)
    }

    private fun stopMediaTimer() {
        mRecTimer?.cancel()
        mRecTimer = null
        mRecHours = 0
        mRecMinute = 0
        mRecSeconds = 0
        mMainHandler.sendEmptyMessage(WHAT_STOP_TIMER)
    }

    private fun calculateTime(seconds: Int, minute: Int, hour: Int = 0): String {
        return String.format("%02d:%02d:%02d", hour, minute, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMediaTimer()
        restTimer?.cancel()
        backgroundExecutor.shutdown()
        if (imageProcessor != null) {
            imageProcessor?.stop()
        }
    }

    companion object {
        private const val TAG = "RecordActivity"
        private const val POSE_DETECTION = "Pose Detection"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val WHAT_START_TIMER = 0x00
        private const val WHAT_STOP_TIMER = 0x01
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        // Exercise class constants from PoseClassifierProcessor
        private const val PUSHUPS_CLASS = "pushups_down"
        private const val SQUATS_CLASS = "squats"
        private const val LUNGES_CLASS = "lunges"
        private const val SITUP_UP_CLASS = "situp_up"
        private const val CHEST_PRESS_CLASS = "chestpress_down"
        private const val DEAD_LIFT_CLASS = "deadlift_down"
        private const val SHOULDER_PRESS_CLASS = "shoulderpress_down"
    }
}