android {
    namespace = "${rootProject.group}.extensions.android"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(project(":core-ktx"))
}
