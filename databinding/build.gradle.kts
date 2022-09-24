android {
    buildFeatures {
        dataBinding = true
    }

    namespace = "${rootProject.group}.databinding"
}

plugins {
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    implementation(libs.androidx.fragment)
}
