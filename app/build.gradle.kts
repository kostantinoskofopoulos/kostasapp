plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.hiltAndroid)
    kotlin("kapt")
}

android {
    namespace = "com.kostas.kostasapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kostas.kostasapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
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

    // ---------------------
    // ANDROID CORE
    // ---------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ---------------------
    // COMPOSE
    // ---------------------
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)

    // ---------------------
    // NAVIGATION 3 + SERIALIZATION
    // ---------------------
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)


    // implementation(libs.androidx.navigation.compose)

    // ---------------------
    // PAGING
    // ---------------------
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // ---------------------
    // HILT
    // ---------------------
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // ---------------------
    // MODULES
    // ---------------------
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:image"))
    implementation(project(":core:common"))

    implementation(project(":feature:heroes"))
    implementation(project(":feature:hero-details"))

    // ---------------------
    // TESTS
    // ---------------------
    testImplementation(libs.junit)

    implementation(libs.timber)
}