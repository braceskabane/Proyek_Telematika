package com.dicoding.hanebado.view.dashboard.anticipation

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.dicoding.hanebado.R

class AnticipationOutOffLayoutDialog : DialogFragment() {

    private var onReadyClickListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_anticipation_out_off_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_ready_to_fast).setOnClickListener {
            onReadyClickListener?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes?.windowAnimations = R.style.DialogAnimation // Ganti dengan animasi yang diinginkan
            setGravity(Gravity.BOTTOM) // Jika Anda ingin dialog muncul dari bawah
        }
    }

    fun setOnReadyClickListener(listener: () -> Unit) {
        onReadyClickListener = listener
    }

    companion object {
        const val TAG = "AnticipationOutOffLayoutDialog"
    }
}
