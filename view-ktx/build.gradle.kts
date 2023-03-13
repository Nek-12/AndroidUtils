android {
    namespace = "${rootProject.group}.extensions.view"
}

dependencies {
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.recyclerview)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.activity)
    api(project(":android-ktx"))
}
