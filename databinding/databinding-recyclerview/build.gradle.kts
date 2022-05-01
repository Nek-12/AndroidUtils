plugins {
    id("org.jetbrains.kotlin.kapt")
}


android {
    buildFeatures {
        dataBinding = true
    }
}


dependencies {
    implementation("androidx.core:core-ktx:${Versions.coreKtx}")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(project(":databinding"))
}
