plugins {
    alias(libs.plugins.kotlinJvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.converter.gson)
}
