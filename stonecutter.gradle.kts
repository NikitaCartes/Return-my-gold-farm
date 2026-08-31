plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}

stonecutter active "26.1-fabric"

stonecutter.tasks {
    order("publishMods")
}

tasks.register<Delete>("cleanCollectedJars") {
    delete(layout.buildDirectory.dir("libs"))
}

// One GitHub release for the whole version matrix: this root task creates it (empty),
// and every node's publishGithub uploads its jar into it via `parent`.
publishMods {
    val githubToken = System.getenv("GITHUB_TOKEN") ?: ""
    val modrinthToken = System.getenv("MODRINTH_TOKEN") ?: ""
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN") ?: ""
    val modVersion = findProperty("mod_version")?.toString()
        ?: file("stonecutter.properties.toml").readLines()
            .first { it.trim().startsWith("mod_version") }
            .substringAfter('=').trim().trim('"')

    dryRun = githubToken.isEmpty() || modrinthToken.isEmpty() || curseforgeToken.isEmpty()
    version = modVersion
    displayName = modVersion
    changelog = rootProject.file("RELEASE_NOTE.md").readText()
    type = STABLE

    github {
        accessToken = githubToken
        repository = "NikitaCartes/Return-my-gold-farm"
        commitish = "master"
        tagName = modVersion
        allowEmptyFiles = true
    }
}
