plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

android {
    namespace = "com.example.socialapp.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.socialapp.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    // later use version catalogue
    implementation("io.github.raamcosta.compose-destinations:core:1.9.51")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.51")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("io.insert-koin:koin-androidx-compose:3.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
//    implementation("androidx.datastore:datastore:1.1.3")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.33.2-alpha")
    implementation("io.coil-kt:coil-compose:2.4.0")
}