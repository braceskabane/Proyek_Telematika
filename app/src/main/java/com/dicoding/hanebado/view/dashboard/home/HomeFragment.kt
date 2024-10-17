package com.dicoding.hanebado.view.dashboard.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.dicoding.hanebado.R
import com.dicoding.hanebado.view.dashboard.reflex.ReflexActivity


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the button in the layout
        val btnStartReflex = view.findViewById<LinearLayout>(R.id.layout_reflex)

        // Set click listener for the button
        btnStartReflex.setOnClickListener {
            // Create an Intent to start ReflexActivity
            val intent = Intent(activity, ReflexActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}