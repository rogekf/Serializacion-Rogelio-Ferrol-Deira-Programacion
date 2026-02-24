plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

// --- ESTO ES LO QUE TE FALTA ---
repositories {
    mavenCentral() // Aquí es donde Gradle buscará las librerías
}

dependencies {
    // La librería para el JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}