import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.versions)
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

detekt {
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins(rootProject.libs.detekt.formatting)
    detektPlugins(rootProject.libs.detekt.compose)
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
        else -> apply(plugin = "android-library")
    }
}

tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        buildUponDefaultConfig = true
        parallel = true
        setSource(projectDir)
        config.setFrom(File(rootDir, Config.Detekt.configFile))
        basePath = projectDir.absolutePath
        jvmTarget = Config.jvmTarget.target
        include(Config.Detekt.includedFiles)
        exclude(Config.Detekt.excludedFiles)
        reports {
            xml.required.set(false)
            html.required.set(true)
            txt.required.set(false)
            sarif.required.set(true)
            md.required.set(false)
        }
    }

    register<io.gitlab.arturbosch.detekt.Detekt>("detektFormat") {
        description = "Formats whole project."
        autoCorrect = true
    }

    register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
        description = "Run detekt on whole project"
        autoCorrect = false
    }

    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>().configureEach {
        outputFormatter = "json"
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

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = Config.jvmTarget.target
        }
    }
}
