dependencies {
    implementation(project(":databinding:databinding-recyclerview"))
    implementation("androidx.paging:paging-runtime:3.0.1")
}

plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
    android {
        buildFeatures {
            dataBinding = true
        }
    }
}
