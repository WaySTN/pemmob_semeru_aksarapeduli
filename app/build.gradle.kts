plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.aksaraaa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aksaraaa"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX & UI (Edge-to-Edge)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0") // Update ke versi terbaru
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Untuk Edge-to-Edge
    implementation("androidx.activity:activity-ktx:1.7.2") // Untuk mengakses WindowCompat
    implementation("androidx.window:window:1.1.0") // Untuk fitur window modern

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.1")
    implementation ("com.google.firebase:firebase-dynamic-links-ktx:21.2.0")

    // Image Libraries
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.activity)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4") // Update ke versi terbaru
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("com.tbuonomo:dotsindicator:4.3")
}

