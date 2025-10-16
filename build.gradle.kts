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
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.tinylog:slf4j-tinylog:2.7.0")
}

kotlin {
    jvmToolchain(20)
}

tasks.test {
    useJUnitPlatform()
}