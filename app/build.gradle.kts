plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.kotlinKsp)
}

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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}