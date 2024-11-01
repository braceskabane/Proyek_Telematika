package com.dicoding.hanebado.view.dashboard.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.hanebado.databinding.FragmentHomeBinding
import com.dicoding.hanebado.view.dashboard.anticipation.AnticipationActivity
import com.dicoding.hanebado.view.dashboard.reflex.ReflexActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set click listeners using binding
        binding.layoutReflex.setOnClickListener {
            val intent = Intent(activity, ReflexActivity::class.java)
            startActivity(intent)
        }

        binding.layoutAnticipation.setOnClickListener {
            val intent = Intent(activity, AnticipationActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
