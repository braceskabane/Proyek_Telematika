package com.dicoding.hanebado.view.dashboard

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.utils.showLongToast
import com.dicoding.hanebado.core.utils.showToast
import com.dicoding.hanebado.databinding.ActivityMainBinding
import com.dicoding.hanebado.view.dashboard.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.common.util.concurrent.ListenableFuture


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private lateinit var splitInstallManager: SplitInstallManager

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        setupBottomNavbar()

        checkNotificationPermission()

        binding.fbRecord.setOnClickListener {
            startRecordActivity()
        }
    }

    private fun startRecordActivity() {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }


    private fun setupBottomNavbar() {
        val navView: BottomNavigationView = binding.bottomNavbar
        val navViewController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.historyFragment,
                R.id.dailyplanFragment,
                R.id.profileFragment
            )
        ).build()

//        setupActionBarWithNavController(navViewController, appBarConfiguration)
        navView.setupWithNavController(navViewController)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navViewController.navigate(R.id.homeFragment)
                }
                R.id.historyFragment -> {
                    navViewController.navigate(R.id.historyFragment)
                }
                R.id.dailyplanFragment -> {
                    navViewController.navigate(R.id.dailyplanFragment)
                }
                R.id.profileFragment -> {
                    navViewController.navigate(R.id.profileFragment)
                }
            }
            true
        }
    }

    private fun checkAndNavigateToFeature(moduleName: String, destinationId: Int, navController: NavController) {
        if (splitInstallManager.installedModules.contains(moduleName)) {
            navController.navigate(destinationId)
        } else {
            val request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build()

            splitInstallManager.startInstall(request)
                .addOnSuccessListener {
                    navController.navigate(destinationId)
                }
                .addOnFailureListener { exception ->
                    showLongToast("Error installing module: $moduleName")
                }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Izin notifikasi diberikan")
            } else {
                showLongToast("Izin tidak diberikan, ini akan mempengaruhi jalannya aplikasi")
            }
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}