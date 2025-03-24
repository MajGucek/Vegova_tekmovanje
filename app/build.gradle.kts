plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.vegova_tekmovanje"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vegova_tekmovanje"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // HelloCharts (if you need this library)
    implementation("com.github.lecho:hellocharts-library:1.5.8")

    // Jetpack Compose
    implementation("androidx.core:core-ktx:1.9.0")  // Use latest version of AndroidX core
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")  // Ensure up-to-date lifecycle library
    implementation("androidx.activity:activity-compose:1.6.0")  // Activity Compose integration
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))  // BOM for Compose version management
    implementation("androidx.compose.ui:ui:1.3.0")  // UI
    implementation("androidx.compose.material3:material3:1.0.0")  // Material3

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.junit:junit:1.1.5")
    androidTestImplementation("androidx.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0")
}
