plugins {
    id("net.fabricmc.fabric-loom")
}

val mod_version = providers.gradleProperty("mod_version").get()
val maven_group = providers.gradleProperty("maven_group").get()

val minecraft_version = providers.gradleProperty("minecraft_version").get()

group = maven_group
version = mod_version

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
}

sourceSets.main {
    resources {
        setSrcDirs(files("src/main/resources", "src/main/generated"))
    }
}
