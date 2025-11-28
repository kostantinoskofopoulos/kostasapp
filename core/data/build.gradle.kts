plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.kostas.kostasapp.core.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
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

    // Core Modules
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    // Paging
    implementation(libs.androidx.paging.runtime)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Gson
    implementation(libs.converter.gson)
    implementation("com.google.code.gson:gson:2.10.1")
}
