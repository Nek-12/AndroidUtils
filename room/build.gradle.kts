val roomVersion = "2.4.0-alpha04"

plugins {
    id("org.jetbrains.kotlin.kapt")
}

android {
//    kapt {
//        arguments {
//            arg("room.schemaLocation", "dbschema/")
//        }
//    }
}

dependencies {
    implementation("androidx.room:room-ktx:${roomVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("androidx.room:room-runtime:${roomVersion}")
    kapt("androidx.room:room-compiler:${roomVersion}")
}
