package com.dicoding.hanebado.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.hanebado.R
import com.dicoding.hanebado.view.dashboard.MainActivity
import com.dicoding.hanebado.view.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionBar()
        checkAuthAndNavigate()
    }

    private fun checkAuthAndNavigate() {
        lifecycleScope.launch {
            delay(DELAY.toLong())

            viewModel.isLoggedIn.collect { isLoggedIn ->
                isLoggedIn?.let {
                    val targetActivity = if (it) {
                        MainActivity::class.java
                    } else {
                        WelcomeActivity::class.java
                    }

                    startActivity(Intent(this@SplashActivity, targetActivity))
                    finish()
                }
            }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    companion object {
        private const val DELAY = 4000
    }
}