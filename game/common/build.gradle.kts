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
    implementation(project(":lib", configuration = "shadow"))
    implementation("github.scarsz:configuralize:1.4.1")
    implementation("com.udojava:EvalEx:2.7")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.3")
}