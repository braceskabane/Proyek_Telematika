package com.dicoding.hanebado.view.dashboard.record

import android.content.Context
import android.util.Log
import com.dicoding.hanebado.core.data.source.local.entity.plan.Plan
import com.dicoding.hanebado.view.dashboard.record.classification.PoseClassifierProcessor
import com.dicoding.hanebado.view.dashboard.record.classification.PostureResult
import com.dicoding.hanebado.view.dashboard.record.graphic.GraphicOverlay
import com.dicoding.hanebado.view.dashboard.record.util.VisionProcessorBase
import com.dicoding.hanebado.view.dashboard.record.graphic.PoseGraphic
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/** A processor to run pose detector. */
class PoseDetectorProcessor(
    private val context: Context,
    options: PoseDetectorOptionsBase,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val runClassification: Boolean,
    private val isStreamMode: Boolean,
    private var cameraXViewModel: CameraXViewModel? = null,
    notCompletedExercise: List<Plan>
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor = Executors.newSingleThreadExecutor()

    private var poseClassifierProcessor: PoseClassifierProcessor? = null
    private var exercisesToDetect: List<String>? = null

    /** Internal class to hold Pose and classification results. */
    inner class PoseWithClassification(
        val pose: Pose,
        classificationResult: Map<String, PostureResult>
    ) {

        init {
            // update live data value
            if (classificationResult.isNotEmpty()) {
                Log.d(TAG, "Updating posture results: $classificationResult")
                cameraXViewModel?.postureLiveData?.postValue(classificationResult)

                // Log setiap PostureResult secara detail
                classificationResult.forEach { (poseName, result) ->
                    Log.d(TAG, "Pose: $poseName - Reps: ${result.repetition}, Confidence: ${result.confidence}")
                }
            }
        }
    }

    init {
        if (notCompletedExercise.isNotEmpty()) {
            exercisesToDetect = notCompletedExercise.map { plan -> plan.exercise }
        }
    }


    override fun stop() {
        super.stop()
        detector.close()
        cameraXViewModel = null
    }

    override fun detectInImage(image: InputImage): Task<PoseWithClassification> {
        return detector
            .process(image)
            .continueWith(
                classificationExecutor
            ) { task ->
                val pose = task.result
                var classificationResult: Map<String, PostureResult> = HashMap()
                if (runClassification) {
                    if (poseClassifierProcessor == null) {
                        Log.d(TAG, "Creating new PoseClassifierProcessor with exercises: $exercisesToDetect")
                        poseClassifierProcessor =
                            PoseClassifierProcessor(
                                context,
                                isStreamMode,
                                exercisesToDetect
                            )
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)

                }
                PoseWithClassification(pose, classificationResult)
            }
    }

    override fun detectInImage(image: MlImage): Task<PoseWithClassification> {
        return detector
            .process(image)
            .continueWith(
                classificationExecutor
            ) { task ->
                val pose = task.result
                var classificationResult: Map<String, PostureResult> = HashMap()
                if (runClassification) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor =
                            PoseClassifierProcessor(
                                context,
                                isStreamMode,
                                exercisesToDetect
                            )
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)
                    Log.d(TAG, "Classification Result: $classificationResult") // Tambahkan log ini
                }
                PoseWithClassification(pose, classificationResult)
            }
    }

    override fun onSuccess(
        poseWithClassification: PoseWithClassification,
        graphicOverlay: GraphicOverlay
    ) {
        try {
            graphicOverlay.add(
                PoseGraphic(
                    graphicOverlay,
                    poseWithClassification.pose,
                    showInFrameLikelihood,
                    visualizeZ,
                    rescaleZForVisualization
                )
            )
            Log.d(TAG, "Successfully added pose graphic to overlay")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding pose graphic", e)
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
        e.printStackTrace()
    }

    override fun isMlImageEnabled(context: Context?): Boolean {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true
    }

    companion object {
        private const val TAG = "PoseDetectorProcessor"
    }
}