plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.141"
}

// Tag this node's loader and version so [neoforge."26.1"] keys resolve via bare property("...").
stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

base.archivesName = "${property("mod_id")}-neoforge-mc${property("display_mc")}"
version = property("mod_version").toString()

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    withSourcesJar()
}

neoForge {
    version = property("neoforge_version").toString()
    mods {
        create("returnmygoldfarm") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

val modExpansions = mapOf(
    "version" to project.version.toString(),
    "supported_minecraft_version" to property("supported_minecraft_version").toString(),
    "neoforge_version" to property("neoforge_version").toString()
)

tasks.processResources {
    inputs.properties(modExpansions)
    filesMatching("META-INF/neoforge.mods.toml") { expand(modExpansions) }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(tasks.named("stonecutterGenerate"))
}

tasks.jar {
    from("LICENSE")
}

tasks.register<Copy>("collectJars") {
    group = "build"
    from(tasks.jar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs"))
    dependsOn("build")
}
