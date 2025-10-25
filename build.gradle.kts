group = "dev.vanadium"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.2.20"
    application
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "avo"
        }
    }
}

application {
    mainClass.set("dev.vanadium.avo.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.20")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.slf4j:slf4j-nop:2.0.9")
    implementation("com.github.ajalt.mordant:mordant:3.0.2")
}

val generateVersionFile by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/kotlin")
    outputs.dir(outputDir)

    doLast {
        val file = outputDir.get().file("BuildVersion.kt").asFile
        file.parentFile.mkdirs()
        file.writeText("""
            package dev.vanadium.avo
            
            object BuildVersion {
                const val VERSION = "$version"
            }
        """.trimIndent())
    }
}

kotlin {
    sourceSets["main"].kotlin.srcDir(layout.buildDirectory.dir("generated/kotlin"))
    jvmToolchain(20)
}

tasks.named("compileKotlin") {
    dependsOn(generateVersionFile)
}

tasks.test {
    useJUnitPlatform()
}