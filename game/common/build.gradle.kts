plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow")
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
    implementation("dev.dejvokep:boosted-yaml:1.3.6")
    implementation("com.udojava:EvalEx:2.7")
    implementation("com.google.guava:guava:33.4.8-jre")
}

tasks.jar {
    enabled = false
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    mergeServiceFiles()
    isZip64 = true
    isPreserveFileTimestamps = true
    isReproducibleFileOrder = true
    manifest {
        attributes["Multi-Release"] = "true"
    }
}

tasks.build {
    dependsOn("shadowJar")
}

kotlin {
    jvmToolchain(8)
}