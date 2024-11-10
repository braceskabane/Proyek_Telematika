package com.dicoding.hanebado.view.otp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.databinding.ActivityOtpBinding
import com.dicoding.hanebado.view.login.LoginActivity
import com.dicoding.hanebado.view.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private val viewModel: OtpViewModel by viewModels()

    private var userId: String = ""
    private var email: String = ""
    private var timer: CountDownTimer? = null
    private var shouldAutoSendOtp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra(LoginActivity.EXTRA_USER_ID) ?: ""
        email = intent.getStringExtra(LoginActivity.EXTRA_EMAIL) ?: ""
        shouldAutoSendOtp = intent.getBooleanExtra(LoginActivity.EXTRA_AUTO_SEND_OTP, false)

        Log.d("OtpActivity", "Received userId: $userId, email: $email, autoSend: $shouldAutoSendOtp")

        if (userId.isEmpty()) {
            Log.e("OtpActivity", "No USER_ID received")
            Toast.makeText(this, "Error: No user ID found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupOtpInputs()
        setupButtons()
        observeOtpResult()
        observeResendOtpResult()

        // Jika dari register (autoSend true), langsung mulai timer karena OTP sudah dikirim
        if (shouldAutoSendOtp) {
            startTimer()  // Langsung start timer karena OTP sudah dikirim saat register
        } else {
            // Jika bukan dari register, perlu request OTP baru
            requestInitialOtp()  // Ini akan request OTP dan memulai timer
        }
    }

    private fun requestInitialOtp() {
        Log.d("OtpActivity", "Requesting initial OTP")
        clearOtpInputs()
        viewModel.resendOTP(userId)
    }

    private fun setupOtpInputs() {
        val editTexts = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6)

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.lastIndex) {
                        editTexts[i + 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Bersihkan timer dan state
        timer?.cancel()
        timer = null

        // Arahkan ke WelcomeActivity dan hapus stack activity sebelumnya
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupButtons() {
        binding.btnOtpContinue.setOnClickListener {
            val otp = getOtpFromInputs()
            if (otp.length == 6) {
                viewModel.activateOtp(userId, otp)
            } else {
                Toast.makeText(this, "Mohon masukkan kode OTP yang valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivOtpRefresh.setOnClickListener {
            Log.d("OtpActivity", "Refresh button clicked")
            clearOtpInputs()
            viewModel.resendOTP(userId)
            startTimer()  // Reset timer setelah OTP di-refresh
            Log.d("OtpActivity", "Resend OTP called with userId: $userId")
        }
    }

    private fun getOtpFromInputs(): String {
        return with(binding) {
            "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"
        }
    }

    private fun startTimer() {
        Log.d("OtpActivity", "Starting timer")
        timer?.cancel()
        timer = null

        Log.d("OtpActivity", "Refresh button disabled for timer")

        timer = object : CountDownTimer(15 * 60 * 1000, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvOtpTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.tvOtpTimer.text = "00:00"
                timer = null
                Log.d("OtpActivity", "Timer finished, refresh button enabled")
            }
        }.start()
    }

    private fun observeOtpResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.otpResult.collect { result ->
                    Log.d("OtpActivity", "OTP result: $result")
                    when (result) {
                        is Resource.Success -> {
                            Log.d("OtpActivity", "OTP activation successful: ${result.data?.message}")
                            Toast.makeText(this@OtpActivity, result.data?.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@OtpActivity, LoginActivity::class.java))
                            finish()
                        }
                        is Resource.Error -> {
                            Log.e("OtpActivity", "OTP activation error: ${result.message}")
                            Toast.makeText(this@OtpActivity, result.message, Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            Log.d("OtpActivity", "OTP activation loading")
                            // Show loading indicator
                        }
                        else -> {
                            Log.w("OtpActivity", "Unexpected OTP result: $result")
                        }
                    }
                }
            }
        }
    }

    private fun observeResendOtpResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resendOtpResult.collect { result ->
                    Log.d("OtpActivity", "Resend OTP result: $result")
                    when (result) {
                        is Resource.Success -> {
                            Log.d("OtpActivity", "OTP resend successful: ${result.data?.message}")
                            Toast.makeText(this@OtpActivity, result.data?.message, Toast.LENGTH_SHORT).show()
                            if (!isTimerRunning()) {
                                startTimer()
                            }
                            clearOtpInputs()
                        }
                        is Resource.Error -> {
                            Log.e("OtpActivity", "OTP resend error: ${result.message}")
                            Toast.makeText(this@OtpActivity, "Failed to resend OTP: ${result.message}", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            Log.d("OtpActivity", "OTP resend loading")
                        }
                        else -> {
                            Log.w("OtpActivity", "Unexpected resend OTP result: $result")
                            if (!isTimerRunning()) {
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isTimerRunning(): Boolean {
        return timer != null
    }

    private fun clearOtpInputs() {
        with(binding) {
            etOtp1.text?.clear()
            etOtp2.text?.clear()
            etOtp3.text?.clear()
            etOtp4.text?.clear()
            etOtp5.text?.clear()
            etOtp6.text?.clear()
            etOtp1.requestFocus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("OtpActivity", "OtpActivity destroyed")
        timer?.cancel()
        timer = null
    }

    companion object {
        const val EXTRA_USER_ID = "USER_ID"
        const val EXTRA_EMAIL = "EMAIL"
        const val EXTRA_AUTO_SEND_OTP = "AUTO_SEND_OTP"
    }
}