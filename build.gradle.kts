plugins {
    kotlin("jvm") version "2.2.20"
}

group = "dev.vanadium"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.20")
    implementation("com.google.code.gson:gson:2.13.2")
}

kotlin {
    jvmToolchain(20)
}

tasks.test {
    useJUnitPlatform()
}