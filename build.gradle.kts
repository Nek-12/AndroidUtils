plugins {
    kotlin("jvm")
    `kotlin-dsl`
    alias(libs.plugins.detekt)
    alias(libs.plugins.version.catalog.update)
}

rootProject.group = Config.group
rootProject.version = Config.version

buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.version.gradle)
    }
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.github.ben-manes.versions")

    detekt {
        buildUponDefaultConfig = true
    }

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
        detektPlugins(rootProject.libs.detekt.compose)
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            xml.required.set(false)
            html.required.set(true)
            txt.required.set(true)
            sarif.required.set(false)
        }
    }
}

versionCatalogUpdate {
    sortByKey.set(false)

    keep {
        keepUnusedVersions.set(true)
        keepUnusedLibraries.set(true)
        keepUnusedPlugins.set(true)
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    when (name) {
        "app" -> apply(plugin = "com.android.application")
        "core-ktx" -> apply(plugin = "java-library")
        else -> apply(plugin = "android-library")
    }
}

tasks {

    register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
        description = "Runs detekt on the project."
        parallel = true
        buildUponDefaultConfig = true
        setSource(file(projectDir))
        config.setFrom(File(rootDir, "detekt.yml"))
        include("**/*.kt", "**/*.kts")
        exclude("**/resources/**", "**/build/**", "**/.idea/**")
        reports {
            xml.required.set(false)
            html.required.set(false)
            txt.required.set(false)
        }
    }
    register<io.gitlab.arturbosch.detekt.Detekt>("detektFormat") {
        description = "Formats whole project."
        parallel = true
        buildUponDefaultConfig = true
        autoCorrect = true
        setSource(file(projectDir))
        config.setFrom(File(rootDir, "detekt.yml"))
        include("**/*.kt", "**/*.kts")
        exclude("**/resources/**", "**/build/**", "**/.idea/**")
        reports {
            xml.required.set(false)
            html.required.set(false)
            txt.required.set(false)
        }
    }

    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>().configureEach {
        // outputFormatter = "json"

        fun stabilityLevel(version: String): Int {
            Config.stabilityLevels.forEachIndexed { index, postfix ->
                val regex = ".*[.\\-]$postfix[.\\-\\d]*".toRegex(RegexOption.IGNORE_CASE)
                if (version.matches(regex)) return index
            }
            return Config.stabilityLevels.size
        }

        rejectVersionIf {
            stabilityLevel(currentVersion) > stabilityLevel(candidate.version)
        }
    }
}
