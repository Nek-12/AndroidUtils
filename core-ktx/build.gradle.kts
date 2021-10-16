import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val compileKotlin: KotlinCompile by tasks

plugins {
    kotlin("jvm")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


compileKotlin.kotlinOptions {
    jvmTarget = "11"
}


publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = rootProject.extra["groupId"].toString()
            artifactId = project.name
            version = "0.1"
            from(components["java"])
        }
    }
}
