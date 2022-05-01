dependencies {
    implementation(project(":databinding:databinding-recyclerview"))
    implementation("androidx.paging:paging-runtime:${Versions.paging}")
}

plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
    buildFeatures {
        dataBinding = true
    }
}
