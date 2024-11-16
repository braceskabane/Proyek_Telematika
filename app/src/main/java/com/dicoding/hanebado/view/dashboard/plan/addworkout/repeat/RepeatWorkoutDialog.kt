package com.dicoding.hanebado.view.dashboard.plan.addworkout.repeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.DialogDailyAddWorkoutRepeatBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepeatWorkoutDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDailyAddWorkoutRepeatBinding
    private val selectedDays = mutableListOf<String>()
    private var isNeverSelected = false
    var daysSelectedListener: OnDaysSelectedListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDailyAddWorkoutRepeatBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setupClickListeners() {
        with(binding) {
            setupDayClick(ClickSunday, ivSunday, "SUNDAY")
            setupDayClick(ClickMonday, ivModay, "MONDAY")
            setupDayClick(ClickTuesday, ivTuesday, "TUESDAY")
            setupDayClick(ClickWednesday, ivWednesday, "WEDNESDAY")
            setupDayClick(ClickThursday, ivThursday, "THURSDAY")
            setupDayClick(ClickFriday, ivFriday, "FRIDAY")
            setupDayClick(ClickSaturday, ivSaturday, "SATURDAY")

            ClickNever.setOnClickListener {
                handleNeverClick()
            }

            btnBack.setOnClickListener {
                val daysToReturn = if (isNeverSelected) emptyList() else selectedDays.toList()
                daysSelectedListener?.onDaysSelected(daysToReturn)
                dismiss()
            }
        }
    }

    private fun handleNeverClick() {
        with(binding) {
            isNeverSelected = !isNeverSelected
            ivNever.visibility = if (isNeverSelected) View.VISIBLE else View.GONE

            // Jika Never dipilih, reset semua pilihan hari
            if (isNeverSelected) {
                selectedDays.clear()
                // Hide semua icon hari
                ivSunday.visibility = View.GONE
                ivModay.visibility = View.GONE
                ivTuesday.visibility = View.GONE
                ivWednesday.visibility = View.GONE
                ivThursday.visibility = View.GONE
                ivFriday.visibility = View.GONE
                ivSaturday.visibility = View.GONE

                // Disable semua click listener hari
                disableDayClicks(true)
            } else {
                // Enable kembali click listener hari
                disableDayClicks(false)
            }
        }
    }

    private fun disableDayClicks(disabled: Boolean) {
        with(binding) {
            ClickSunday.isEnabled = !disabled
            ClickMonday.isEnabled = !disabled
            ClickTuesday.isEnabled = !disabled
            ClickWednesday.isEnabled = !disabled
            ClickThursday.isEnabled = !disabled
            ClickFriday.isEnabled = !disabled
            ClickSaturday.isEnabled = !disabled
        }
    }

    private fun setupDayClick(clickView: View, iconView: ImageView, dayName: String) {
        clickView.setOnClickListener {
            if (!isNeverSelected) {
                if (iconView.visibility == View.VISIBLE) {
                    iconView.visibility = View.GONE
                    selectedDays.remove(dayName)
                } else {
                    iconView.visibility = View.VISIBLE
                    selectedDays.add(dayName)
                    // Jika hari dipilih, pastikan Never tidak terpilih
                    binding.ivNever.visibility = View.GONE
                    isNeverSelected = false
                }
            }
        }
    }

    companion object {
        const val TAG = "RepeatWorkoutDialog"
    }
}