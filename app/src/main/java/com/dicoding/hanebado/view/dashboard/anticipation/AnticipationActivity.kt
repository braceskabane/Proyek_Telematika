package com.dicoding.hanebado.view.dashboard.anticipation

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.hanebado.databinding.ActivityAnticipationBinding

class AnticipationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnticipationBinding
    private var isAnimationStopped = false
    private var ballFinalY: Float = 0f
    private var animator: ValueAnimator? = null
    private var lastBallYPosition: Float = 0f
    private var lastTouchTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnticipationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        showPrepareDialog()
    }

    private fun initializeViews() {
        // Sembunyikan semua view di awal
        binding.apply {
            ball.visibility = View.INVISIBLE
            topLine.visibility = View.INVISIBLE
            midLine.visibility = View.INVISIBLE
            bottomLine.visibility = View.INVISIBLE
        }
        setupClickListener()
    }

    private fun resetAnimation() {
        // Reset semua state dan view
        isAnimationStopped = false
        animator?.cancel()
        animator = null

        binding.apply {
            ball.clearAnimation()
            ball.translationY = 0f
            ball.visibility = View.INVISIBLE
            topLine.visibility = View.INVISIBLE
            midLine.visibility = View.INVISIBLE
            bottomLine.visibility = View.INVISIBLE
        }
    }

    private fun calculateDeviation() {
        val deviation = ballFinalY - binding.bottomLine.y + 36

        val deviationText = when {
            deviation > 0 -> "+${deviation.toInt()} px"  // Bola sudah melewati midLine
            deviation < 0 -> "${deviation.toInt()} px"   // Bola belum mencapai midLine
            else -> "0 px"                               // Bola persis di midLine
        }

        // Tambahkan info waktu ke dialog hasil
        showDoneDialog("$deviationText")
    }


    private fun showDoneDialog(result: String) {
        val dialog = AnticipationDoneDialog.newInstance(result).apply {
            setOnReadyNextClickListener {
                // Reset dan mulai animasi baru
                resetAndStartNewAnimation()
            }
        }

        dialog.show(supportFragmentManager, AnticipationDoneDialog.TAG)
    }

    private fun resetAndStartNewAnimation() {
        resetAnimation()
        binding.root.post {
            startAnimation()
        }
    }

    private fun startAnimation() {
        binding.ball.visibility = View.VISIBLE
        val totalDistance = binding.root.height - binding.topLine.y

        animator = ValueAnimator.ofFloat(0f, totalDistance).apply {
            duration = 5000 // durasi animasi, misalnya 5 detik
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                if (!isAnimationStopped) {
                    val value = animation.animatedValue as Float
                    binding.ball.translationY = value

                    // Cek apakah bola sudah mencapai lowestLine
                    if (binding.ball.y >= binding.lowestLine.y) {
                        isAnimationStopped = true
                        animator?.cancel()
                        showOutOfLayoutDialog()
                    } else {
                        // Logika tampilan seperti yang ada sebelumnya
                        if (binding.ball.y < binding.midLine.y) {
                            binding.topLine.visibility = View.INVISIBLE
                            binding.midLine.visibility = View.INVISIBLE
                            binding.bottomLine.visibility = View.INVISIBLE
                            binding.ball.visibility = View.VISIBLE
                        } else {
                            binding.topLine.visibility = View.VISIBLE
                            binding.midLine.visibility = View.VISIBLE
                            binding.bottomLine.visibility = View.VISIBLE
                            binding.ball.visibility = View.GONE
                        }
                    }

                    ballFinalY = binding.ball.y
                }
            }
        }

        animator?.start()
    }

    private fun showOutOfLayoutDialog() {
        val dialog = AnticipationOutOffLayoutDialog().apply {
            setOnReadyClickListener {
                resetAndStartNewAnimation()
            }
        }

        dialog.show(supportFragmentManager, "AnticipationOutOffLayoutDialog")
    }



    private fun setupClickListener() {
        binding.main.setOnClickListener {
            if (!isAnimationStopped && animator?.isRunning == true) {
                isAnimationStopped = true
                animator?.cancel()

                lastTouchTime = System.currentTimeMillis()  // Simpan waktu saat layar ditekan
                lastBallYPosition = binding.ball.y // Simpan posisi terakhir bola di lastBallYPosition

                binding.apply {
                    ball.visibility = View.VISIBLE
                    topLine.visibility = View.VISIBLE
                    midLine.visibility = View.VISIBLE
                    bottomLine.visibility = View.VISIBLE
                }

                calculateDeviation()
            }
        }
    }


    private fun showPrepareDialog() {
        val dialog = AnticipationPrepareDialog().apply {
            setOnReadyClickListener {
                // Mulai animasi setelah tombol ready ditekan
                binding.root.post {
                    startAnimation()
                }
            }
        }

        dialog.show(supportFragmentManager, AnticipationPrepareDialog.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
    }
}