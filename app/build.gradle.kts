plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.kotlinSerialization)   // 🔥 REQUIRED for Navigation 3 typed routes
    alias(libs.plugins.hiltAndroid)
    kotlin("kapt")
}

android {
    namespace = "com.kostas.kostasapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kostas.kostasapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()  // 🔥 FIXED
    }

    packaging {
        resources.excludes += "/META-INF/{LICENSE*,NOTICE*,DEPENDENCIES}"
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

    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)

    // Navigation (Typed Routes)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Modules
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))

    implementation(project(":feature:heroes"))
    implementation(project(":feature:hero-details"))

    // Testing
    testImplementation(libs.junit)
}