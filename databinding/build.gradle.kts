android {
    buildFeatures {
        dataBinding = true
    }
}

plugins {
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    implementation(libs.androidx.fragment)
}
