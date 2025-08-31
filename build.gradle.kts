plugins {
    kotlin("jvm") version "2.2.0"
}

group = "me.minecraftauth"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://nexus.scarsz.me/content/groups/public/")
            name = "nexus-scarsz"
        }
    }
}
