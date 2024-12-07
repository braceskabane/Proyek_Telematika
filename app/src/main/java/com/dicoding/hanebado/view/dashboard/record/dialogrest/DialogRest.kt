package com.dicoding.hanebado.view.dashboard.record.dialogrest

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.DialogRestBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// RestDialog.kt
class RestDialog(
    private val nextSet: Int,
    private val onContinueClicked: () -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: DialogRestBinding? = null
    private val binding get() = _binding!!
    private var countDownTimer: CountDownTimer? = null

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTimer()
        setupClickListeners()
    }

    private fun setupTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvTimer.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                dismiss()
            }
        }.start()
    }

    private fun setupClickListeners() {
        binding.btnContinue.setOnClickListener {
            countDownTimer?.cancel()
            onContinueClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }

    companion object {
        const val TAG = "RestDialog"
    }
}