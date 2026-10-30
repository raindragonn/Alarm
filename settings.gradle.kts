pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
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

rootProject.name = "Alarm"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:network")
include(":core:datastore")
include(":core:designsystem")
