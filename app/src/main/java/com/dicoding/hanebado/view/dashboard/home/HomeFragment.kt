package com.dicoding.hanebado.view.dashboard.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.databinding.FragmentHomeBinding
import com.dicoding.hanebado.view.dashboard.anticipation.AnticipationActivity
import com.dicoding.hanebado.view.dashboard.reflex.ReflexActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var todayPlanAdapter: TodayPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Click listeners
        binding.layoutReflex.setOnClickListener {
            val intent = Intent(activity, ReflexActivity::class.java)
            startActivity(intent)
        }

        binding.layoutAnticipation.setOnClickListener {
            val intent = Intent(activity, AnticipationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        todayPlanAdapter = TodayPlanAdapter()
        binding.rvProgressLabel.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todayPlanAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.todayPlan.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading state if needed
                    }
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            todayPlanAdapter.submitList(data)
                        }
                    }
                    is Resource.Error -> {
                        // Handle error state
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
