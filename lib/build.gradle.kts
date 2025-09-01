@file:Suppress("LocalVariableName")

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.gradleup.shadow")
}

group = "me.minecraftauth"
version = "1.0"

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

tasks.jar {
    enabled = false
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("io.ktor", "me.minecraftauth.lib.libraries.ktor")
    relocate("kotlinx", "me.minecraftauth.lib.libraries.kotlinx")
    relocate("org.bouncycastle", "me.minecraftauth.lib.libraries.bouncycastle")

    include("io/ktor/**")
    include("kotlinx/serialization/**")
    include("kotlinx/coroutines/**")
    include("kotlinx/io/**")
    include("kotlin/reflect/**")
    include("META-INF/services/io.ktor.**")
    include("META-INF/services/kotlinx.serialization.**")
    include("org/bouncycastle/**")
    include("me/minecraftauth/**")
}

tasks.build {
    dependsOn("dokkaGenerateModuleHtml", "shadowJar")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}
