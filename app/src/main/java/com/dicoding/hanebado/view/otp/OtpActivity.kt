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

        // Enable refresh button initially
        binding.ivOtpRefresh.isEnabled = true

        setupOtpInputs()
        setupButtons()
        observeOtpResult()
        observeResendOtpResult()

        // Jika perlu auto send, langsung request OTP
        if (shouldAutoSendOtp) {
            requestInitialOtp()
        }else {

            startTimer()
        }
    }

    private fun requestInitialOtp() {
        Log.d("OtpActivity", "Requesting initial OTP")
        clearOtpInputs()
        binding.ivOtpRefresh.isEnabled = false
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
            // Disable button sementara
            binding.ivOtpRefresh.isEnabled = false
            // Clear input fields
            clearOtpInputs()
            // Request resend
            viewModel.resendOTP(userId)
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

        binding.ivOtpRefresh.isEnabled = false // Disable refresh button

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
                binding.ivOtpRefresh.isEnabled = true // Enable refresh button
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
                            startTimer() // Timer akan men-disable button
                            clearOtpInputs()
                        }
                        is Resource.Error -> {
                            Log.e("OtpActivity", "OTP resend error: ${result.message}")
                            Toast.makeText(this@OtpActivity, "Failed to resend OTP: ${result.message}", Toast.LENGTH_SHORT).show()
                            binding.ivOtpRefresh.isEnabled = true // Enable button jika error
                            timer?.cancel() // Cancel timer jika error
                            binding.tvOtpTimer.text = "00:00" // Reset timer display
                        }
                        is Resource.Loading -> {
                            Log.d("OtpActivity", "OTP resend loading")
                            binding.ivOtpRefresh.isEnabled = false
                        }
                        else -> {
                            Log.w("OtpActivity", "Unexpected resend OTP result: $result")
                            binding.ivOtpRefresh.isEnabled = true
                            timer?.cancel()
                            binding.tvOtpTimer.text = "00:00"
                        }
                    }
                }
            }
        }
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