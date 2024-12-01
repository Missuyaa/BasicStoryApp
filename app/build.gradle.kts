plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    id("kotlin-kapt")

}

android {
    namespace = "com.dicoding.storyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dicoding.storyapp"
        minSdk = 24
        targetSdk = 34
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
    // Android Core Dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose Core
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // AppCompat and Material Design
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // ConstraintLayout (Jetpack Compose and XML)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // Retrofit for Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for Networking and Debugging
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Glide for Image Loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // Coil for Jetpack Compose Image Loading
    implementation("io.coil-kt:coil-compose:2.3.0")

    // RecyclerView (If needed for XML-based UI lists)
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // DataStore for Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Gson (JSON Parsing)
    implementation("com.google.code.gson:gson:2.10.1")

    // Room Database for Local Storage
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")

    // CameraX for Camera Features
    implementation("androidx.camera:camera-core:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-view:1.2.3")

    // Accompanist Libraries for Compose
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation("com.google.accompanist:accompanist-placeholder:0.30.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")

    // Coroutines for Asynchronous Programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Paging Compose for Infinite Scroll
    implementation("androidx.paging:paging-compose:3.2.0")

    // Shimmer Effect for Loading Placeholders
    implementation("com.valentinilk.shimmer:compose-shimmer:1.0.3")

    // Lifecycle Components for ViewModel in Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Compose Animation
    implementation("androidx.compose.animation:animation:1.6.0")

    // Testing Libraries
    testImplementation(libs.junit)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging Tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
