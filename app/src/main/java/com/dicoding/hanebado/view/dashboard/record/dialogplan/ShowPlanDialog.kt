package com.dicoding.hanebado.view.dashboard.record.dialogplan

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain
import com.dicoding.hanebado.databinding.DialogRecordDailyWorkoutBinding
import com.dicoding.hanebado.view.dashboard.record.OnTodayExerciseSelectedListener
import com.dicoding.hanebado.view.dashboard.record.RecordActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowPlanDialog : BottomSheetDialogFragment() {
    var exerciseSelectedListener: OnTodayExerciseSelectedListener? = null
    private var _binding: DialogRecordDailyWorkoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowPlanViewModel by viewModels()
    private lateinit var workoutAdapter: RecordDailyWorkoutAdapter
    var onDismissListener: (() -> Unit)? = null

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRecordDailyWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeTodayPlan()
    }

    private fun setupRecyclerView() {
        workoutAdapter = RecordDailyWorkoutAdapter().apply {
            setOnReadyClickListener { exercise ->
                // Handle click pada button Ready
                startExercise(exercise)
                dismiss()
            }
        }

        binding.rvStartWorkout.apply {
            adapter = workoutAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun startExercise(exercise: TodayExerciseDomain) {
        // Intent ke RecordActivity atau handle sesuai kebutuhan
        startActivity(Intent(requireContext(), RecordActivity::class.java).apply {
            putExtra("exerciseId", exercise.id)
        })
        dismiss()
    }

    private fun observeTodayPlan() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayPlan.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { plan ->
                                Log.d("ShowPlanDialog", "Received plan with ${plan.exercises.size} exercises")
                                workoutAdapter.submitList(plan.exercises)
                                updatePlanHeader(plan)
                            }
                        }
                        is Resource.Loading -> {
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                context,
                                result.message ?: "Error loading today's plan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun updatePlanHeader(plan: TodayDailyPlanDomain) {
        binding.apply {
            tvWorkoutLabel.text = plan.label
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ShowPlanDialog"
    }
}