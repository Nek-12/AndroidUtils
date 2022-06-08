android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get().toString()
        useLiveLiterals = true
    }
}

dependencies {
    implementation(project(":android-ktx"))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.graphics)
    implementation(libs.lifecycle.runtime)
}
