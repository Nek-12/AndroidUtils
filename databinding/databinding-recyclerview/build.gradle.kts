plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.recyclerview)
    implementation(project(":databinding"))
}
