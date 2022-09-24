dependencies {
    implementation(libs.androidx.core)
    implementation(project(":core-ktx"))
}

android {
    namespace = "${rootProject.group}.extensions.android"
}
