@file:Suppress("MissingPackageDeclaration")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// must be top-level

object Config {

    const val group = "com.nek12.androidutils"
    const val name = "androidutils"
    const val minSdk = 22
    const val compileSdk = 33
    val jvmTarget = JvmTarget.JVM_11
    const val version = "1.0.2"
    const val buildToolsVersion = "33.0.0"

    val kotlinCompilerArgs = listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xjvm-default=all",
        "-Xbackend-threads=0", // parallel IR compilation
        "-opt-in=kotlin.Experimental",
        "-opt-in=kotlin.RequiresOptIn",
        "-Xcontext-receivers",
    )

    val stabilityLevels = listOf("preview", "eap", "alpha", "beta", "m", "cr", "rc")
    object Detekt {

        const val configFile = "detekt.yml"
        val includedFiles = listOf("**/*.kt", "**/*.kts")
        val excludedFiles = listOf("**/resources/**", "**/build/**", "**/.idea/**")
    }
}
