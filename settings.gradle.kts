pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kostasapp"
include(":app")



// --- CORE ---
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:domain")
include(":core:data")
include(":core:designsystem")

// --- FEATURE ---
include(":feature:heroes")
include(":feature:hero-details")