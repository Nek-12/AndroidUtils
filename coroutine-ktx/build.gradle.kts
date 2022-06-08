dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    api(project(":core-ktx"))
}
