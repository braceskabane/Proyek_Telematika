package com.dicoding.hanebado.view.dashboard.plan.addworkout
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.exercise.model.SelectedExerciseData
import com.dicoding.hanebado.databinding.DialogDailyAddWorkoutBinding
import com.dicoding.hanebado.view.dashboard.plan.DailyPlanRefreshListener
import com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout.CustomWorkoutDialog
import com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout.OnExerciseSelectedListener
import com.dicoding.hanebado.view.dashboard.plan.addworkout.repeat.OnDaysSelectedListener
import com.dicoding.hanebado.view.dashboard.plan.addworkout.repeat.RepeatWorkoutDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddWorkoutDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDailyAddWorkoutBinding
    private var selectedExercises = mutableListOf<SelectedExerciseData>()
    private var selectedDays = mutableListOf<String>()
    override fun getTheme(): Int = R.style.CustomBottomSheetDialog
    private val viewModel: AddWorkoutViewModel by viewModels()
    var refreshListener: DailyPlanRefreshListener? = null

    // Default notification time
    private val defaultNotificationTime = "2024-11-11T06:00:00Z"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDailyAddWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAuthenticationStatus()
        setUpListeners()
        observeAddDailyPlan()
    }

    private fun checkAuthenticationStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isAuthenticated.collect { isAuthenticated ->
                Log.d("AddWorkoutDialog", "Checking auth status: $isAuthenticated")
                if (!isAuthenticated) {
                    // Refresh and check again
                    viewModel.refreshAuthStatus()
                    delay(300)

                    if (!viewModel.isAuthenticated.value) {
                        Log.d("AddWorkoutDialog", "User not authenticated")
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                } else {
                    Log.d("AddWorkoutDialog", "User is authenticated")
                }
            }
        }
    }

    private fun setUpListeners() {
        binding.apply {
            btnAddTagSave.setOnClickListener {
                if (validateInput()) {
                    saveWorkout()
                }
            }
            binding.btnAddTagCancel.setOnClickListener {
                dismiss()
            }
            llCustomWorkout.setOnClickListener{
                showCustomWorkoutDialog()
            }
            llRepeat.setOnClickListener{
                showRepeatWorkoutDialog()
            }

            etLabel.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // Optional: Lakukan validasi realtime jika diperlukan
                }
            })
        }
    }

    // Di AddWorkoutDialog
    private fun showCustomWorkoutDialog() {
        val dialog = CustomWorkoutDialog().apply {
            exerciseSelectedListener = object : OnExerciseSelectedListener {
                override fun onExerciseSelected(exercises: List<SelectedExerciseData>) {
                    Log.d("AddWorkoutDialog", "Selected exercises: ${
                        exercises.joinToString("\n") {
                            "ID: ${it.exerciseId}, Sets: ${it.sets}, Reps: ${it.reps}, Order: ${it.order}"
                        }
                    }")
                    selectedExercises.clear()
                    selectedExercises.addAll(exercises)
                    updateSelectedExercisesUI()
                }
            }
        }
        dialog.show(childFragmentManager, CustomWorkoutDialog.TAG)
    }

    private fun updateSelectedExercisesUI() {
        binding.tvExerciseCount.text = "${selectedExercises.size} Exercise"
        binding.tvExerciseCount.visibility = if (selectedExercises.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun showRepeatWorkoutDialog() {
        val dialog = RepeatWorkoutDialog().apply {
            daysSelectedListener = object : OnDaysSelectedListener {
                override fun onDaysSelected(days: List<String>) {
                    Log.d("AddWorkoutDialog", "Selected days: $days")
                    selectedDays.clear()
                    selectedDays.addAll(days)
                    updateSelectedDaysUI()
                }
            }
        }
        dialog.show(childFragmentManager, RepeatWorkoutDialog.TAG)
    }

    private fun updateSelectedDaysUI() {
        binding.apply {
            if (selectedDays.isNotEmpty()) {
                tvSelectedDays.text = "${selectedDays.size} Days"
                tvSelectedDays.visibility = View.VISIBLE
            } else {
                tvSelectedDays.text = "Never"
                tvSelectedDays.visibility = View.VISIBLE
            }
        }
    }

    private fun validateInput(): Boolean {
        val label = binding.etLabel.text.toString().trim()

        when {
            label.isEmpty() -> {
                binding.etLabel.error = "Please enter a label"
                return false
            }
            selectedExercises.isEmpty() -> {
                Toast.makeText(context, "Please select exercises", Toast.LENGTH_SHORT).show()
                return false
            }
            selectedDays.isEmpty() -> {
                Toast.makeText(context, "Please select repeat days", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun saveWorkout() {
        if (!validateInput()) return

        val label = binding.etLabel.text.toString().trim()
        showLoading(true)

        // Data untuk dikirim ke API
        val workoutData = mapOf(
            "notificationTime" to defaultNotificationTime,
            "repeatDays" to selectedDays,
            "label" to label,
            "exercises" to selectedExercises.map { exercise ->
                mapOf(
                    "exerciseId" to exercise.exerciseId,
                    "sets" to exercise.sets,
                    "reps" to exercise.reps,
                    "order" to exercise.order
                )
            },
        )

        // Log data untuk verifikasi
        Log.d("AddWorkoutDialog", "Workout Data: $workoutData")

        // Optional: Tampilkan loading state
        showLoading(true)

        viewModel.addDailyPlan(
            label = label,
            selectedDays = selectedDays,
            selectedExercises = selectedExercises
        )
    }

    private fun observeAddDailyPlan() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addDailyPlanResult.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        showLoading(false)
                        Toast.makeText(context, "Workout plan added successfully", Toast.LENGTH_SHORT).show()
                        Log.d("AddWorkoutDialog", "Save successful, triggering refresh")
                        refreshListener?.onDailyPlanAdded() // Trigger refresh di parent
                        dismiss()
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            context,
                            result.message ?: "Failed to add workout plan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                        showLoading(false)
                    }
                    is Resource.Message -> {
                        showLoading(false)
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (!isAdded || view == null) return

        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnAddTagSave.isEnabled = !isLoading
            btnAddTagCancel.isEnabled = !isLoading
            etLabel.isEnabled = !isLoading
            llCustomWorkout.isEnabled = !isLoading
            llRepeat.isEnabled = !isLoading
        }
    }

    companion object {
        const val TAG = "AddWorkoutDialog"
    }
}