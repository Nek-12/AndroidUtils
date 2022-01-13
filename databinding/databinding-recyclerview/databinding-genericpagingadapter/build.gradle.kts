dependencies {
    implementation(project(":databinding:databinding-recyclerview"))
    implementation("androidx.paging:paging-runtime:3.1.0")
}

plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
    buildFeatures {
        dataBinding = true
    }
}
