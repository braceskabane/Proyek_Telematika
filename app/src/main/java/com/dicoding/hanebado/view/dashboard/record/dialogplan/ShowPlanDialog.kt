package com.dicoding.hanebado.view.dashboard.record.dialogplan

import android.content.DialogInterface
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowPlanDialog : BottomSheetDialogFragment() {
    var exerciseSelectedListener: OnTodayExerciseSelectedListener? = null
    private var _binding: DialogRecordDailyWorkoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowPlanViewModel by viewModels()
    private lateinit var workoutAdapter: DailyPlanAdapter
    private var onDismissListener: (() -> Unit)? = null

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
        workoutAdapter = DailyPlanAdapter(
            onExerciseClickListener = { exercise ->
                handleExerciseSelection(exercise)
            },
            onReadyClickListener = { exercise ->
                handleExerciseSelection(exercise)
            }
        )

        binding.rvStartWorkout.apply {
            adapter = workoutAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleExerciseSelection(exercise: TodayExerciseDomain) {
        exerciseSelectedListener?.onExerciseSelected(exercise)
        dismiss()
    }

    private fun observeTodayPlan() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayPlan.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { planList ->
                                Log.d("ShowPlanDialog", "Received plan with ${planList.size} daily plans")
                                // Memperbarui header dengan daftar
                                updatePlanHeader(planList)
                                workoutAdapter.setDailyPlans(planList)
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

    private fun updatePlanHeader(planList: List<TodayDailyPlanDomain>) {
        binding.apply {
            // Memeriksa apakah ada elemen dalam daftar dan mengambil elemen pertama
            val firstPlan = planList.firstOrNull()
            firstPlan?.let {
                tvWorkoutLabel.text = it.label
            } ?: run {
                // Menangani kasus jika tidak ada data dalam daftar
                tvWorkoutLabel.text = "No plan available"
            }
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