pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

include(":room")
include(":android-ktx")
include(":material-ktx")
include(":preferences-ktx")
include(":coroutine-ktx")
include(":safenavcontroller")
include(":core-ktx")
include(":databinding")
include(":databinding:databinding-recyclerview")
include(":databinding:databinding-recyclerview:databinding-genericpagingadapter")
include(":viewbinding")
include(":compose-ktx")
include(":view-ktx")
