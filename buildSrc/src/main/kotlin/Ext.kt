@file:Suppress("MissingPackageDeclaration")

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.plugin.use.PluginDependency

fun VersionCatalog.requirePlugin(alias: String) = findPlugin(alias).get().toString()
fun VersionCatalog.requireLib(alias: String) = findLibrary(alias).get()
fun VersionCatalog.requireBundle(alias: String) = findBundle(alias).get()

val org.gradle.api.provider.Provider<PluginDependency>.id: String get() = get().pluginId
