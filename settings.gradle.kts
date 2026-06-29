pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
}

// 26.1 and 26.2 are byte-identical for everything this mod touches, so one jar per loader covers
// both. Each node builds against a stable base and declares the full 26.1-26.2 range in its manifest.
stonecutter {
    create(rootProject) {
        versions("26.2-fabric" to "26.2").buildscript("build.fabric.gradle.kts")
        versions("26.1-neoforge" to "26.1").buildscript("build.neoforge.gradle.kts")
        vcsVersion = "26.2-fabric"
    }
}
