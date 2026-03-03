plugins {
    kotlin("jvm") version "2.0.20" // Ajustado a versión estable común
    kotlin("plugin.serialization") version "2.0.20"
    application // <--- 1. AGREGA ESTO
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

application {
    mainClass.set("org.example.MainKt")
}
