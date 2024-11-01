package com.dicoding.hanebado.view.dashboard.anticipation

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.hanebado.R

class AnticipationActivity : AppCompatActivity() {
    private lateinit var ballImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anticipation)

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ball ImageView
        ballImageView = findViewById(R.id.ball)

        // Load and start animation
        startBallAnimation()
    }

    private fun startBallAnimation() {
        // Load animation from XML
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.custom_bounce)

        // Start the animation on the ball
        ballImageView.startAnimation(bounceAnimation)
    }

    override fun onPause() {
        super.onPause()
        // Clear animation when activity is paused
        ballImageView.clearAnimation()
    }

    override fun onResume() {
        super.onResume()
        // Restart animation when activity is resumed
        startBallAnimation()
    }
}