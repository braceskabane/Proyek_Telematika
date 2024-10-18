package com.dicoding.hanebado.view.dashboard.reflex

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.hanebado.R
import kotlin.random.Random

class ReflexActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private val AUDIO_DELAY = 1600L
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var isSoundPlayed = false
    private var repeatCount = 0
    private var reactionTimes = mutableListOf<Long>()
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var soundRunnable: Runnable
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView
    private val TOTAL_ROUNDS = 5
    private val bellSoundDuration = 3000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reflex)

        progressBar = findViewById(R.id.pb_reflex)
        tvProgress = findViewById(R.id.tv_pb_reflex)

        initializeMediaPlayer()

        updateProgress(0)
        showPrepareDialog()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ring_bell)
        mediaPlayer.setOnCompletionListener {
            isSoundPlayed = false
        }
    }

    private fun updateProgress(currentRound: Int) {
        val progress = (currentRound * 100) / TOTAL_ROUNDS
        progressBar.progress = progress
        tvProgress.text = "$progress%"
        Log.d("ReflexActivity", "Progress updated: $progress%")
    }

    private fun startReflexTest() {
        Log.d("ReflexActivity", "Starting reflex test")
        repeatCount = 0
        reactionTimes.clear()
        updateProgress(0)
        nextRound()
    }

    private fun stopSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare() // Prepare it for the next play
        }
        Log.d("ReflexActivity", "Sound stopped")
    }

    private fun showPrepareDialog() {
        Log.d("ReflexActivity", "Showing prepare dialog")
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_reflex_prepare, null)
        dialog.setContentView(view)

        val btnReady = view.findViewById<Button>(R.id.btn_ready)
        btnReady.setOnClickListener {
            Log.d("ReflexActivity", "Ready button pressed")
            dialog.dismiss()
            startReflexTest()
        }

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        dialog.show()
    }

    private fun nextRound() {
        if (repeatCount >= TOTAL_ROUNDS) {
            Log.d("ReflexActivity", "All rounds completed, showing final results")
            showDoneDialog()
            return
        }

        updateProgress(repeatCount)
        Log.d("ReflexActivity", "Starting round ${repeatCount + 1}")
        isSoundPlayed = false
        val initialDelay = 3000L
        val randomDelay = Random.nextLong(0, 12000)
        val totalDelay = initialDelay + randomDelay

        Log.d("ReflexActivity", "Bell will ring after $totalDelay ms")

        soundRunnable = Runnable {
            playSound()
        }

        handler.postDelayed(soundRunnable, totalDelay)
    }

    private fun playSound() {
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
        startTime = System.nanoTime()
        isSoundPlayed = true
        Log.d("ReflexActivity", "Bell sound started, timing begins")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            Log.d("ReflexActivity", "Screen touched")
            handler.removeCallbacks(soundRunnable)
            stopSound()
            if (!isSoundPlayed) {
                Log.d("ReflexActivity", "Touched too early")
                showTooFastDialog()
            } else {
                val endTime = System.nanoTime()
                var reactionTime = (endTime - startTime) / 1_000_000 // Convert to milliseconds
                reactionTime = maxOf(0, reactionTime - AUDIO_DELAY) // Compensate for audio delay
                Log.d("ReflexActivity", "Reaction time (compensated): $reactionTime ms")
                reactionTimes.add(reactionTime)

                repeatCount++
                updateProgress(repeatCount)

                if (repeatCount < TOTAL_ROUNDS) {
                    showSetDoneDialog(reactionTime)
                } else {
                    showDoneDialog()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun showTooFastDialog() {
        stopSound()
        Log.d("ReflexActivity", "Showing too fast dialog")
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_reflex_to_fast, null)
        dialog.setContentView(view)

        val btnReady = view.findViewById<Button>(R.id.btn_ready_to_fast)
        btnReady.setOnClickListener {
            Log.d("ReflexActivity", "Ready to retry")
            dialog.dismiss()
            nextRound()
        }

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        dialog.show()
    }

    private fun showSetDoneDialog(reactionTime: Long) {
        Log.d("ReflexActivity", "Showing set done dialog")
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_reflex_set_done, null)
        dialog.setContentView(view)

        val tvRecentTime = view.findViewById<TextView>(R.id.tv_recent_time)
        tvRecentTime.text = "$reactionTime ms"

        val fastestTime = reactionTimes.minOrNull() ?: reactionTime
        val tvFastestTime = view.findViewById<TextView>(R.id.tv_fastest_time)
        tvFastestTime.text = "Fastest: $fastestTime ms"

        val btnReady = view.findViewById<Button>(R.id.btn_ready_next)
        btnReady.setOnClickListener {
            Log.d("ReflexActivity", "Ready for next round")
            dialog.dismiss()
            nextRound()
        }

        val btnReset = view.findViewById<Button>(R.id.btn_reset)
        btnReset.setOnClickListener {
            Log.d("ReflexActivity", "Resetting the game")
            dialog.dismiss()
            resetGame()
        }

        val btnSound = view.findViewById<Button>(R.id.btn_sound)
        btnSound.setOnClickListener {
            Log.d("ReflexActivity", "Playing bell sound")
            playBellSound()
        }

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        dialog.show()
    }

    private fun resetGame() {
        repeatCount = 0
        reactionTimes.clear()
        updateProgress(0)
        showPrepareDialog()
    }

    private fun playBellSound() {
        mediaPlayer.seekTo(0) // Rewind to the start
        mediaPlayer.start()
        Log.d("ReflexActivity", "Bell sound started")

        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0) // Rewind for next play
                Log.d("ReflexActivity", "Bell sound stopped after 2 seconds")
            }
        }, bellSoundDuration)
    }

    private fun showDoneDialog() {
        Log.d("ReflexActivity", "Showing final results dialog")
        val averageTime = reactionTimes.average().toLong()
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_reflex_done, null)
        dialog.setContentView(view)

        val tvResult = view.findViewById<TextView>(R.id.tv_result)
        val (category, description) = getCategoryAndDescription(averageTime)
        tvResult.text = category

        val tvDescription = view.findViewById<TextView>(R.id.tv_result_description)
        tvDescription.text = description

        val tvResultMean = view.findViewById<TextView>(R.id.tv_result_mean)
        tvResultMean.text = getAverageCategoryDescription(averageTime)

        Log.d("ReflexActivity", "Average time: $averageTime ms, Category: $category")

        val btnReady = view.findViewById<Button>(R.id.btn_well_done)
        btnReady.setOnClickListener {
            Log.d("ReflexActivity", "Test completed, returning to Home Fragment")
            dialog.dismiss()
            finish() // Return to HomeFragment
        }

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        dialog.show()
    }

    private fun getCategoryAndDescription(time: Long): Pair<String, String> {
        return when {
            time < 100 -> Pair("Formula 1 Driver", "Lightning-fast reflexes, processing information at superhuman speeds.")
            time < 150 -> Pair("Soccer Goalkeeper", "Split-second decision-maker, anticipating and reacting to shots with incredible speed.")
            time < 200 -> Pair("Table Tennis Player", "Razor-sharp focus, responding to high-velocity shots in the blink of an eye.")
            time < 250 -> Pair("Sprinter", "Explosive starter, transforming auditory cues into instant physical action.")
            time < 300 -> Pair("Archer", "Precision timer, balancing patience with quick release for optimal accuracy.")
            else -> Pair("Marathon Runner", "Endurance master, prioritizing sustained pace over rapid reactions.")
        }
    }

    private fun getAverageCategoryDescription(averageTime: Long): String {
        return when {
            averageTime < 100 -> "< 100 ms"
            averageTime < 150 -> "< 150 ms"
            averageTime < 200 -> "< 200 ms"
            averageTime < 250 -> "< 250 ms"
            averageTime < 300 -> "< 300 ms"
            else -> "≥ 300 ms"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ReflexActivity", "Activity destroyed")
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}