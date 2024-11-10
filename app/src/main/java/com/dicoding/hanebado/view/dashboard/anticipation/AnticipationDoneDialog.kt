package com.dicoding.hanebado.view.dashboard.anticipation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.DialogAnticipationSetDoneBinding

class AnticipationDoneDialog : DialogFragment() {
    private lateinit var binding: DialogAnticipationSetDoneBinding
    private var onReadyNextClick: (() -> Unit)? = null
    private var onResetClick: (() -> Unit)? = null
    private var onSoundClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAnticipationSetDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set hasil jarak
        arguments?.getString(DISTANCE_RESULT)?.let {
            binding.tvRecentDistance.text = it
        }

        // Set listener untuk tombol
        binding.btnReadyNext.setOnClickListener {
            dismiss()
            onReadyNextClick?.invoke()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimationUpperFirst
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
        }
    }

    fun setOnReadyNextClickListener(listener: () -> Unit) {
        onReadyNextClick = listener
    }

    fun setOnResetClickListener(listener: () -> Unit) {
        onResetClick = listener
    }

    fun setOnSoundClickListener(listener: () -> Unit) {
        onSoundClick = listener
    }

    companion object {
        const val TAG = "AnticipationDoneDialog"
        private const val DISTANCE_RESULT = "distance_result"

        fun newInstance(distanceResult: String): AnticipationDoneDialog {
            return AnticipationDoneDialog().apply {
                arguments = Bundle().apply {
                    putString(DISTANCE_RESULT, distanceResult)
                }
            }
        }
    }
}