package com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.databinding.DialogDailyAddWorkoutCustomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomWorkoutDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDailyAddWorkoutCustomBinding
    private lateinit var exerciseAdapter: ExerciseAdapter
    var exerciseSelectedListener: OnExerciseSelectedListener? = null

    private val viewModel: ExerciseViewModel by viewModels()

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDailyAddWorkoutCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setUpListeners()
        observeExercises()

        // Trigger fetch exercises
//        viewModel.getExercises()
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter()
        binding.rvExercises.apply {
            adapter = exerciseAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeExercises() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exercises.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        result.data?.let { exercises ->
                            // Langsung menggunakan model Exercise tanpa konversi
                            exerciseAdapter.submitList(exercises)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            result.message ?: "Error fetching exercises",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Message -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setUpListeners() {
        binding.btnSave.setOnClickListener {
            val selectedExercises = exerciseAdapter.getSelectedExercises()
            exerciseSelectedListener?.onExerciseSelected(selectedExercises)
            dismiss()
        }
        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "CustomWorkoutDialog"
    }
}