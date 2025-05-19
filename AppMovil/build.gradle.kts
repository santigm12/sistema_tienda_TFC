// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    repositories{
        google()
    }

    dependencies{
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.5")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("kapt") version "2.0.21"
}