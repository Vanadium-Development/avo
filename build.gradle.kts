group = "dev.vanadium"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.2.20"
    application
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
    implementation("com.github.ajalt.mordant:mordant:3.0.2")
}

kotlin {
    jvmToolchain(20)
}

tasks.test {
    useJUnitPlatform()
}