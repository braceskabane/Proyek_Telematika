package com.dicoding.hanebado.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.utils.isInternetAvailable
import com.dicoding.hanebado.core.utils.showToast
import com.dicoding.hanebado.databinding.ActivityLoginBinding
import com.dicoding.hanebado.view.dashboard.MainActivity
import com.dicoding.hanebado.view.otp.OtpActivity
import com.dicoding.hanebado.view.register.RegisterActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isButtonEnabled(false)
        handleEditText()
        observeLoginResult()
        observeActivationState()
    }

    private fun observeActivationState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.activationState.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            result.data?.let { data ->
                                Log.d("LoginActivity", "Received activation data: id=${data.id}, isActivated=${data.isActivated}")
                                if (!data.isActivated) {
                                    navigateToOtpActivity(data.id, data.email)
                                } else {
                                    proceedWithLogin()
                                }
                            }
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            isButtonEnabled(true)
                            handleLoginError(result.message)
                        }
                        else -> {
                            showLoading(false)
                            isButtonEnabled(true)
                        }
                    }
                }
            }
        }
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.TransparentBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.dialog_error_login_register, null)
        bottomSheetDialog.setContentView(view)

        val tvErrorMessage = view.findViewById<TextView>(R.id.puError)
        tvErrorMessage.text = errorMessage

        Log.d("LoginActivity", "Showing error bottom sheet with message: $errorMessage")

        bottomSheetDialog.show()
    }

    private fun handleEditText() {
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

        binding.edLoginPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

        binding.layoutLoginPass.setEndIconOnClickListener {
            if (binding.edLoginPass.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.edLoginPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.layoutLoginPass.endIconDrawable = getDrawable(R.drawable.icons_no_see_pass)
            } else {
                binding.edLoginPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.layoutLoginPass.endIconDrawable = getDrawable(R.drawable.icons_see_pass)
            }

            binding.edLoginPass.setSelection(binding.edLoginPass.text!!.length)
        }
    }

    private fun checkForms() {
        binding.apply {
            val email = edLoginEmail.text.toString()
            val pass = edLoginPass.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutLoginEmail.error = getString(R.string.wrong_email_format)
            } else {
                binding.layoutLoginEmail.error = null
            }

            if (pass.length < 8) {
                binding.layoutLoginPass.error = getString(R.string.wrong_password_format)
            } else {
                binding.layoutLoginPass.error = null
            }

            isButtonEnabled(
                email.isNotEmpty()
                        && pass.isNotEmpty()
                        && pass.length >= 8
                        && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        Log.d("LoginActivity", "isButtonEnabled called with: $isEnabled")
        binding.btnLogin.isEnabled = isEnabled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun observeLoginResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginResult.collect { result ->
                    when (result) {
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            showLoading(false)
                            isButtonEnabled(true)
                            handleSuccessfulLogin(result.data)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            isButtonEnabled(true)
                            handleLoginError(result.message)
                        }
                        else -> {}
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            startLogin()
        }

        binding.tvRegister.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun startLogin() {
        if (!isInternetAvailable(this)) {
            showErrorBottomSheet(getString(R.string.check_internet))
            return
        }

        val email = binding.edLoginEmail.text.toString()
        Log.d("LoginActivity", "Starting activation check for email: $email")
        showLoading(true)
        isButtonEnabled(false)
        loginViewModel.checkActivation(email)
    }

    private fun proceedWithLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPass.text.toString()
        Log.d("LoginActivity", "Proceeding with login for email: $email")
        loginViewModel.login(email, password)
    }

    private fun handleLoginError(errorMessage: String?) {
        Log.d("LoginActivity", "Error message received: $errorMessage")
        if (!isInternetAvailable(this)) {
            showErrorBottomSheet(getString(R.string.check_internet))
        } else {
            showErrorBottomSheet(errorMessage ?: getString(R.string.check_data))
        }
    }

    private fun handleSuccessfulLogin(loginData: LoginDomain?) {
        val token = loginData?.accessToken
        if (token != null) {
            loginViewModel.saveLoginStatus(true)
            loginViewModel.saveAccessToken(token)
            showLoading(false)
            navigateToMainActivity()
        } else {
            showLoading(false)
            showToast(getString(R.string.login_failed_no_token))
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToOtpActivity(userId: String, email: String) {
        Log.d("LoginActivity", "Navigating to OTP with userId: $userId, email: $email")
        val intent = Intent(this, OtpActivity::class.java).apply {
            putExtra(EXTRA_USER_ID, userId)
            putExtra(EXTRA_EMAIL, email)
            putExtra(EXTRA_AUTO_SEND_OTP, true) // Dari login, langsung kirim OTP
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_USER_ID = "USER_ID"
        const val EXTRA_EMAIL = "EMAIL"
        const val EXTRA_AUTO_SEND_OTP = "AUTO_SEND_OTP"
    }
}