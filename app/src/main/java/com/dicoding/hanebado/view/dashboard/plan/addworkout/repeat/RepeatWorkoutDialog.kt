package com.dicoding.hanebado.view.dashboard.plan.addworkout.repeat

import AddWorkoutDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.DialogDailyAddWorkoutCustomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomWorkoutDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDailyAddWorkoutCustomBinding

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogDailyAddWorkoutCustomBinding.inflate(inflater, container, false)
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
        const val TAG = "CustomWorkoutDialog"
    }
}