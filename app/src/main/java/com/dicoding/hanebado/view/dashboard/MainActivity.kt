package com.dicoding.hanebado.view.dashboard

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.utils.showLongToast
import com.dicoding.hanebado.core.utils.showToast
import com.dicoding.hanebado.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private lateinit var splitInstallManager: SplitInstallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        setupBottomNavbar()

        checkNotificationPermission()
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
}

//class MainActivity : AppCompatActivity(), SensorEventListener {
//
//    private lateinit var sensorManager: SensorManager
//    private var accelerometer: Sensor? = null
//    private var gyroscope: Sensor? = null
//
//    private lateinit var accelerometerChartX: LineChart
//    private lateinit var accelerometerChartY: LineChart
//    private lateinit var accelerometerChartZ: LineChart
//    private lateinit var gyroscopeChartX: LineChart
//    private lateinit var gyroscopeChartY: LineChart
//    private lateinit var gyroscopeChartZ: LineChart
//
//    // Menyimpan data untuk grafik
//    private val accXEntries = ArrayList<Entry>()
//    private val accYEntries = ArrayList<Entry>()
//    private val accZEntries = ArrayList<Entry>()
//    private val gyroXEntries = ArrayList<Entry>()
//    private val gyroYEntries = ArrayList<Entry>()
//    private val gyroZEntries = ArrayList<Entry>()
//
//    // Buffer untuk menyimpan data sebelum motion
//    private val bufferSize = 50 // Menyimpan 50 data terakhir sebelum motion
//    private val accXBuffer = ArrayList<Float>()
//    private val accYBuffer = ArrayList<Float>()
//    private val accZBuffer = ArrayList<Float>()
//
//    private val gyroXBuffer = ArrayList<Float>()
//    private val gyroYBuffer = ArrayList<Float>()
//    private val gyroZBuffer = ArrayList<Float>()
//
//    private var time: Float = 0f // untuk sumbu waktu
//
//    // Threshold untuk deteksi motion
//    private val motionThreshold = 1.5f
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Inisialisasi grafik
//        accelerometerChartX = findViewById(R.id.accelerometerChartX)
//        accelerometerChartY = findViewById(R.id.accelerometerChartY)
//        accelerometerChartZ = findViewById(R.id.accelerometerChartZ)
//        gyroscopeChartX = findViewById(R.id.gyroscopeChartX)
//        gyroscopeChartY = findViewById(R.id.gyroscopeChartY)
//        gyroscopeChartZ = findViewById(R.id.gyroscopeChartZ)
//
//        // Inisialisasi SensorManager
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//
//        // Daftarkan listener untuk sensor
//        accelerometer?.also { acc ->
//            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
//        }
//        gyroscope?.also { gyro ->
//            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL)
//        }
//
//        setupChart(accelerometerChartX)
//        setupChart(accelerometerChartY)
//        setupChart(accelerometerChartZ)
//        setupChart(gyroscopeChartX)
//        setupChart(gyroscopeChartY)
//        setupChart(gyroscopeChartZ)
//    }
//
//    private fun setupChart(chart: LineChart) {
//        chart.data = LineData()
//        chart.setTouchEnabled(false) // Nonaktifkan interaksi pengguna
//        chart.description.isEnabled = false
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        event?.let {
//            time += 0.1f // Update waktu untuk setiap data baru
//
//            when (it.sensor.type) {
//                Sensor.TYPE_ACCELEROMETER -> {
//                    val accX = it.values[0]
//                    val accY = it.values[1]
//                    val accZ = it.values[2]
//
//                    updateBuffer(accXBuffer, accX)
//                    updateBuffer(accYBuffer, accY)
//                    updateBuffer(accZBuffer, accZ)
//
//                    if (detectMotion(accX, accY, accZ)) {
//                        // Jika ada motion, tambahkan data sebelum dan sesudah ke grafik
//                        addBufferToChart(accelerometerChartX, accXEntries, accXBuffer)
//                        addBufferToChart(accelerometerChartY, accYEntries, accYBuffer)
//                        addBufferToChart(accelerometerChartZ, accZEntries, accZBuffer)
//
//                        updateChart(accelerometerChartX, accXEntries, accX)
//                        updateChart(accelerometerChartY, accYEntries, accY)
//                        updateChart(accelerometerChartZ, accZEntries, accZ)
//                    }
//                }
//                Sensor.TYPE_GYROSCOPE -> {
//                    val gyroX = it.values[0]
//                    val gyroY = it.values[1]
//                    val gyroZ = it.values[2]
//
//                    updateBuffer(gyroXBuffer, gyroX)
//                    updateBuffer(gyroYBuffer, gyroY)
//                    updateBuffer(gyroZBuffer, gyroZ)
//
//                    if (detectMotion(gyroX, gyroY, gyroZ)) {
//                        // Jika ada motion, tambahkan data sebelum dan sesudah ke grafik
//                        addBufferToChart(gyroscopeChartX, gyroXEntries, gyroXBuffer)
//                        addBufferToChart(gyroscopeChartY, gyroYEntries, gyroYBuffer)
//                        addBufferToChart(gyroscopeChartZ, gyroZEntries, gyroZBuffer)
//
//                        updateChart(gyroscopeChartX, gyroXEntries, gyroX)
//                        updateChart(gyroscopeChartY, gyroYEntries, gyroY)
//                        updateChart(gyroscopeChartZ, gyroZEntries, gyroZ)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun updateBuffer(buffer: ArrayList<Float>, value: Float) {
//        if (buffer.size >= bufferSize) {
//            buffer.removeAt(0) // Hapus data terlama jika buffer penuh
//        }
//        buffer.add(value)
//    }
//
//    private fun detectMotion(x: Float, y: Float, z: Float): Boolean {
//        // Deteksi motion berdasarkan threshold
//        return abs(x) > motionThreshold || abs(y) > motionThreshold || abs(z) > motionThreshold
//    }
//
//    private fun addBufferToChart(chart: LineChart, entries: ArrayList<Entry>, buffer: ArrayList<Float>) {
//        val currentTime = time - buffer.size * 0.1f // Sesuaikan waktu untuk data buffer
//        for (i in buffer.indices) {
//            entries.add(Entry(currentTime + i * 0.1f, buffer[i]))
//        }
//    }
//
//    private fun updateChart(chart: LineChart, entries: ArrayList<Entry>, value: Float) {
//        // Misalkan kita menambahkan offset 10 agar semua nilai menjadi positif
//        val offset = 10f
//        val adjustedValue = value + offset
//        entries.add(Entry(time, adjustedValue)) // Tambah data baru
//
//        val dataSet = LineDataSet(entries, "Data")
//        chart.data = LineData(dataSet)
//        chart.notifyDataSetChanged() // Beritahu chart bahwa ada data baru
//        chart.invalidate() // Refresh chart
//    }
//
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Tidak digunakan
//    }
//
//    override fun onResume() {
//        super.onResume()
//        accelerometer?.also { acc ->
//            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
//        }
//        gyroscope?.also { gyro ->
//            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL)
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        sensorManager.unregisterListener(this)
//    }
//}


