/**
 * LEARNING NOTE:
 * This is the project-level build.gradle.kts file.
 * It defines the build plugins used by the project and their versions via the Version Catalog.
 * By setting 'apply false', we register the plugins for download, but we don't apply them to the root project itself.
 * The subprojects (like the ':app' module) will apply them individually in their build files.
 */

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
}
