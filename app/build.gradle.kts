plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
	id("kotlin-kapt")
	
}

android {
    namespace = "com.example.equipoocho"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.equipoocho"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
		vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
	
	buildFeatures {
        viewBinding = true
        dataBinding = true 
    }
	
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
	
	// AndroidX UI
	implementation("androidx.core:core-ktx:1.13.1")
	implementation("androidx.appcompat:appcompat:1.7.0")
	implementation("com.google.android.material:material:1.12.0")
	implementation("androidx.recyclerview:recyclerview:1.3.2")


	// Lifecycle + MVVM
	implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
	implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")


	// Room
	implementation ("androidx.room:room-ktx:2.6.1")
	//kapt ("androidx.room:room-compiler:2.6.1")


	// Coroutines
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")


	// Biometric
	implementation ("androidx.biometric:biometric:1.2.0-alpha05")


	// Lottie
	implementation ("com.airbnb.android:lottie:6.4.0")
}