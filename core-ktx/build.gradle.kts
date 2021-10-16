plugins {
    id ("kotlin")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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
