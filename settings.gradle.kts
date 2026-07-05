/**
 * LEARNING NOTE:
 * This is the project's settings file written in Kotlin DSL.
 * It defines where Gradle should look for plugins and dependencies (Google, Maven Central).
 * It also defines the root project name ("DeenCompanion") and registers all subprojects (the ':app' module).
 */

pluginManagement {
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

rootProject.name = "DeenCompanion"
include(":app")
