@file:Suppress("MissingPackageDeclaration") // must be top-level

object Config {

    const val group = "com.nek12.androidutils"
    const val minSdk = 22
    const val compileSdk = 33
    const val jvmTarget = "11"
    const val version = "0.7.15"

    val kotlinCompilerArgs = listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xjvm-default=all",
        "-Xbackend-threads=0", // parallel IR compilation
        "-opt-in=kotlin.Experimental",
        "-opt-in=kotlin.RequiresOptIn",
    )

    val stabilityLevels = listOf("preview", "eap", "alpha", "beta", "m", "cr", "rc")
}
