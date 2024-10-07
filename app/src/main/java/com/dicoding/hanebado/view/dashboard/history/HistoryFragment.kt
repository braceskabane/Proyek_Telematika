package com.dicoding.hanebado.view.dashboard.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HistoryPagerAdapter(requireActivity())
        binding.vpHistory.adapter = adapter

        binding.btnWorkout.setOnClickListener {
            binding.vpHistory.currentItem = 0
            setButtonState(binding.btnWorkout, true)
            setButtonState(binding.btnReflex, false)
        }

        binding.btnReflex.setOnClickListener {
            binding.vpHistory.currentItem = 1
            setButtonState(binding.btnWorkout, false)
            setButtonState(binding.btnReflex, true)
        }

        binding.vpHistory.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        setButtonState(binding.btnWorkout, true)
                        setButtonState(binding.btnReflex, false)
                    }
                    1 -> {
                        setButtonState(binding.btnWorkout, false)
                        setButtonState(binding.btnReflex, true)
                    }
                }
            }
        })
    }

    private fun setButtonState(button: Button, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundResource(R.drawable.custom_button_history_workout)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            button.setBackgroundResource(R.drawable.custom_button_history_reflex_no_border)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}