android {
    namespace = "${rootProject.group}.extensions.coroutines"
}

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
}
