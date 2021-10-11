plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0-M1")

}
