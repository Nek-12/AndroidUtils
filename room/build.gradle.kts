val roomVersion = "2.4.0"

plugins {
    id("com.google.devtools.ksp").version("${Versions.kotlin}-${Versions.ksp}")
}

dependencies {
    implementation("androidx.room:room-ktx:${roomVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}")
    implementation("androidx.room:room-runtime:${roomVersion}")
    ksp("androidx.room:room-compiler:${roomVersion}")
}
