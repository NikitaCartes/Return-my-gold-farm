plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.147"
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}

stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

val javaVersion = property("java_version").toString().toInt()

base.archivesName = "${property("mod_id")}-neoforge-mc${property("minecraft_version")}"
version = property("mod_version").toString()

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    withSourcesJar()
}

neoForge {
    version = property("neoforge_version").toString()
    mods {
        // The NeoForge mod id has no dashes, unlike mod_id used for the Fabric id and jar name.
        create("returnmygoldfarm") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

val modExpansions = mapOf(
    "version" to project.version.toString(),
    "mod_id" to property("mod_id").toString(),
    "mod_name" to property("mod_name").toString(),
    "supported_minecraft_version" to property("supported_minecraft_version").toString(),
    "neoforge_version" to property("neoforge_version").toString(),
    "java_version" to javaVersion.toString()
)

tasks.processResources {
    inputs.properties(modExpansions)
    exclude("fabric.mod.json")
    filesMatching("META-INF/neoforge.mods.toml") { expand(modExpansions) }
    filesMatching("return-my-gold-farm.mixins.json") { expand(modExpansions) }
}

// Stonecutter + NeoForge: generated sources must exist before the MC artifacts are built.
tasks.named("createMinecraftArtifacts") {
    dependsOn(tasks.named("stonecutterGenerate"))
}

tasks.jar {
    from(rootProject.file("LICENSE"))
}

tasks.register<Copy>("collectJars") {
    group = "build"
    from(tasks.jar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs"))
    dependsOn("build", rootProject.tasks.named("cleanCollectedJars"))
}

publishMods {
    val modrinthToken = System.getenv("MODRINTH_TOKEN") ?: ""
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN") ?: ""
    val githubToken = System.getenv("GITHUB_TOKEN") ?: ""

    file = tasks.jar.get().archiveFile
    dryRun = modrinthToken.isEmpty() || curseforgeToken.isEmpty() || githubToken.isEmpty()
    displayName = "${property("display_name")} ${project.version}"
    version = project.version.toString()
    changelog = rootProject.file("RELEASE_NOTE.md").readText()
    type = STABLE
    modLoaders.add("neoforge")

    val targets = property("supported_versions").toString().split(",")
    modrinth {
        projectId = "kuRpWzg6"
        accessToken = modrinthToken
        targets.forEach(minecraftVersions::add)
    }
    curseforge {
        projectId = "1156147"
        accessToken = curseforgeToken
        targets.forEach(minecraftVersions::add)
        client.set(true)
        server.set(true)
    }
    github {
        accessToken = githubToken
        parent(rootProject.tasks.named("publishGithub"))
    }
}
