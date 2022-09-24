plugins {
    alias(libs.plugins.ksp)
}

android {
    namespace = "${rootProject.group}.room"
}

dependencies {
    implementation(libs.room.ktx)
    implementation(libs.kotlin.coroutines)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    testImplementation(libs.bundles.unittest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.android.test.runner)
    androidTestImplementation(libs.android.junit)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.kotlin.coroutines.test)
}
