@file:Suppress("LocalVariableName")

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "me.minecraftauth"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")

	val ktor_version: String by project
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    testImplementation(kotlin("test"))
}

tasks.build {
    dependsOn("dokkaGenerateModuleHtml")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}
