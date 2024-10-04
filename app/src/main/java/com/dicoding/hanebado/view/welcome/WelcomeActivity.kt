package com.dicoding.hanebado.view.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.ActivityWelcomeBinding
import com.dicoding.hanebado.view.login.LoginActivity
import com.dicoding.hanebado.view.register.RegisterActivity
import me.relex.circleindicator.CircleIndicator3

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sliderpage()
        setupAction()
        setupActionBar()
    }

    private fun sliderpage(){
        val titles = listOf("Page 1", "Page 2", "Page 3")
        val descriptions = listOf(
            "Track your badminton life in one place.",
            "Make progress toward goals.",
            "Show your true strength to the enemies"
        )
        val images = listOf(R.drawable.test_image_a, R.drawable.test_image_a, R.drawable.test_image_a)
        val phones = listOf(R.drawable.test_phone_a, R.drawable.test_phone_a, R.drawable.test_phone_a)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager2)

        val adapter = ViewPagerAdapter(titles, descriptions, images, phones)
        viewPager.adapter = adapter

        val circleIndicator = findViewById<CircleIndicator3>(R.id.circleIndicator3)
        circleIndicator.setViewPager(viewPager)
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }
}