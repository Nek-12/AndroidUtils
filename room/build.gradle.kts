val roomVersion = "2.4.0-beta01"

plugins {
    id("com.google.devtools.ksp").version("1.5.31-1.0.0")
}

dependencies {
    implementation("androidx.room:room-ktx:${roomVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}")
    implementation("androidx.room:room-runtime:${roomVersion}")
    ksp("androidx.room:room-compiler:${roomVersion}")
}
