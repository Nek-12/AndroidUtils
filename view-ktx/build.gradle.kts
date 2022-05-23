dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    implementation("androidx.recyclerview:recyclerview:${Versions.recyclerview}")
    implementation("androidx.fragment:fragment-ktx:${Versions.fragmentKtx}")
    implementation("androidx.activity:activity-ktx:${Versions.activity}")
    api(project(":android-ktx"))
    api(project(":core-ktx"))
}
