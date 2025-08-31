import java.net.URL

plugins {
    kotlin("jvm") version "2.2.0"
}

group = "me.minecraftauth.game"
version = "1.0"

kotlin {
    jvmToolchain(8)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":lib"))
    implementation("github.scarsz:configuralize:1.4.1")
}