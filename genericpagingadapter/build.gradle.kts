dependencies {
    implementation(project(":recyclerview"))
    implementation("androidx.paging:paging-runtime:3.0.1")
}



android {
    android {
        buildFeatures {
            dataBinding = true
        }
    }
}
