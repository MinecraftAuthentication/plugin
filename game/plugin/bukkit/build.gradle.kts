plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow")
}

group = "me.minecraftauth.plugin"
version = project(":game:common").version

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/repository/public/") {
        name = "spigotmc-repo"
    }
}

dependencies {
    implementation(project(":game:common"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.shadowJar {
    mergeServiceFiles()
    minimize()
    archiveClassifier.set("")
    archiveFileName.set("MCAuth-Bukkit-${project.version}.jar")

    doLast {
        val targetDir = file("$rootDir/.libs/plugin")
        targetDir.mkdirs()
        val outputFile = archiveFile.get().asFile
        copy {
            from(outputFile)
            into(targetDir)
        }
    }
}

tasks.build {
    dependsOn("shadowJar")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
