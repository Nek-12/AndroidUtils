plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
    buildFeatures {
        dataBinding = true
    }

    namespace = "${rootProject.group}.databinding.recyclerview"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.recyclerview)
    implementation(project(":databinding"))
}
