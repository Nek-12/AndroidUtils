plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":databinding:databinding-recyclerview"))
    implementation(libs.paging)
}
