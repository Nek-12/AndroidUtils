android {
    buildFeatures {
        viewBinding = true
    }

    namespace = "${rootProject.group}.viewbinding"
}

dependencies {
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.core)
}
