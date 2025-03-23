import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

    alias(libs.plugins.kotlinSerialization)
}

repositories {
    google()
    mavenCentral() // Required for Koin
}


val coroutinesVersion = "1.6.4"
val koinVersion = "3.3.2"
val ktorVersion = "2.2.1"
val dataStoreVersion = "1.1.3"

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            api("io.insert-koin:koin-core:$koinVersion")

            implementation("androidx.datastore:datastore-preferences-core:$dataStoreVersion")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            api("io.insert-koin:koin-android:$koinVersion")
            implementation("io.ktor:ktor-client-android:$ktorVersion")
            api("androidx.datastore:datastore-preferences:$dataStoreVersion")

        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            implementation("com.squareup.okio:okio:3.0.0")
        }
    }
//    jvmToolchain("1.8")
}

android {
    namespace = "com.example.socialapp"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

