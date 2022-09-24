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
    implementation(project(":databinding:databinding-recyclerview"))
    implementation(libs.paging)
}
