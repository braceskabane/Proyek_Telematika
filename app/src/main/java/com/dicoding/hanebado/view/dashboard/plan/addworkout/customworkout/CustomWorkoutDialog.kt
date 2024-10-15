package com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout

import AddWorkoutDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.DialogDailyAddWorkoutRepeatBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RepeatWorkoutDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDailyAddWorkoutRepeatBinding

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogDailyAddWorkoutRepeatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btnBack.setOnClickListener {
            showAddWorkoutDialog()
            dismiss()
        }
    }

    private fun showAddWorkoutDialog() {
        val dialog = AddWorkoutDialog()
        dialog.show(childFragmentManager, AddWorkoutDialog.TAG)
    }

    companion object {
        const val TAG = "RepeatWorkoutDialog"
    }
}