// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Evita fallos de snapshot/bloqueo en OneDrive moviendo outputs de build a AppData local.
val localBuildRoot =
    java.io.File(System.getenv("LOCALAPPDATA") ?: System.getProperty("java.io.tmpdir"), "HeatTourism-build")

allprojects {
    val safeProjectPath = project.path.replace(':', '_').ifBlank { "root" }
    layout.buildDirectory.set(localBuildRoot.resolve(safeProjectPath))
}
