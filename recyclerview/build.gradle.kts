

android {
    buildFeatures {
        dataBinding = true
    }
}


dependencies {
    implementation("androidx.core:core-ktx:1.7.0-beta02")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(project(":databinding"))
}
