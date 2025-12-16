pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kostasapp"

// APP
include(":app")

// CORE
include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":core:network")
include(":core:common")
include(":core:image")

// FEATURES
include(":feature:heroes")
include(":feature:hero-details")