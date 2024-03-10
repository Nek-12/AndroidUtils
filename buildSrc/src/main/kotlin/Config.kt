@file:Suppress("MissingPackageDeclaration")

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// must be top-level

object Config {

    const val group = "com.nek12"
    const val artifact = "androidutils"
    const val artifactId = "$group.$artifact"

    const val minSdk = 23
    const val compileSdk = 34
    val jvmTarget = JvmTarget.JVM_11
    val javaVersion = JavaVersion.VERSION_11
    val kotlinVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9
    const val version = "1.1.2"

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
