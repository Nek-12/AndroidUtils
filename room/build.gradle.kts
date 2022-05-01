plugins {
    id("com.google.devtools.ksp").version("${Versions.kotlin}-${Versions.ksp}")
}

dependencies {
    implementation("androidx.room:room-ktx:${Versions.room}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}")
    implementation("androidx.room:room-runtime:${Versions.room}")
    ksp("androidx.room:room-compiler:${Versions.room}")
}
