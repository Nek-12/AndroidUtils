android {
    buildFeatures {
        dataBinding = true
    }
}

plugins {
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    implementation("androidx.fragment:fragment-ktx:${Versions.fragmentKtx}")
}
