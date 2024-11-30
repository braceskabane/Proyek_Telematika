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
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://androidx.dev/snapshots/builds/1.0.0-SNAPSHOT/artifacts/repository/") } // Tambahan untuk SNAPSHOT
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://androidx.dev/snapshots/builds/1.0.0-SNAPSHOT/artifacts/repository/") } // Tambahan untuk SNAPSHOT
    }
}

rootProject.name = "Hanebado"
include(":app")
