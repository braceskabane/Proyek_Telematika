plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.download)
}

// Set the asset directory path
extra["ASSET_DIR"] = "$projectDir/src/main/assets"
apply(from = "../download_tasks.gradle")

android {
    namespace = "com.dicoding.hanebado"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dicoding.hanebado"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
//        buildConfigField("String", "BASE_URL", "\"http://192.168.70.201:8080\"")
//        buildConfigField("String", "BASE_URL", "\"http://192.168.69.201:8080\"")
//        buildConfigField("String", "BASE_URL", "\"http://192.168.30.49:8080\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
    aaptOptions {
        noCompress.add("tflite")
    }
}

dependencies {

    // Default
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.gson)

    // Chart
    implementation(libs.mpandroidchart)

    // Circle Indicator
    implementation(libs.circleindicator)

    // Local
    implementation(libs.room.runtime)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.feature.delivery.ktx)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
//    implementation(libs.litert.support.api)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.room.testing)

    // Remote
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.logging.interceptor)

    // Coroutine
    implementation(libs.room.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.lifecycle.livedata.ktx)

    // Dagger Hilt
    implementation(libs.fragment.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.ksp)

    // Preference
    implementation(libs.datastore)
    implementation(libs.datastore.core)

    // Glide
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Circle Image
    implementation(libs.circleimageview)

    // LeakCanary
//    implementation(libs.leakcanary)

    // Camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // Encryption
    implementation (libs.security.crypto)
    implementation (libs.secure.preferences.lib)
    implementation(libs.sqlcipher)

    // MediaPipe Tasks Vision
    implementation(libs.mediapipe.tasks.vision)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.gpu)
    implementation (libs.tensorflow.lite.task.vision)

    // ML Kit
    implementation(libs.mlkit.common)
    implementation(libs.mlkit.pose.detection)
    implementation(libs.mlkit.pose.detection.accurate)
    implementation(libs.mlkit.camera)
    implementation(libs.mlkit.vision.common)
    implementation(libs.mlkit.image.labeling)
//    implementation(libs.mlkit.face.detection)

    // CameraX
    implementation (libs.camerax.camera2)
    implementation (libs.camerax.lifecycle)
    implementation (libs.camerax.view)
    implementation (libs.odml.image)
    implementation (libs.camerax.extensions)

    // UI Components
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation(libs.viewpager2)
    implementation(libs.dotsindicator)
    implementation(libs.curved.bottom.navigation)

    // Additional Libraries
    implementation(libs.android.gif.drawable)
    implementation(libs.guava)
    implementation(libs.multidex)
    implementation(libs.uiautomator)
    implementation(libs.odml.image)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

