package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PokemonListResponse(val results: List<PokemonSimple>)

@Serializable
data class PokemonSimple(val name: String, val url: String)

@Serializable
data class PokemonDetail(
    val id: Int,
    val name: String,
    val base_experience: Int = 0,
    val height: Int = 0,
    val weight: Int = 0,
    val types: List<TypeSlot> = emptyList()
)

@Serializable
data class TypeSlot(val type: TypeName)

@Serializable
data class TypeName(val name: String)

fun main() {
    val cliente = HttpClient.newHttpClient()
    val jsonConfig = Json { ignoreUnknownKeys = true }

    val urlLista = "https://pokeapi.co/api/v2/pokemon?limit=1025"
    val peticionLista = HttpRequest.newBuilder().uri(URI.create(urlLista)).GET().build()

    println("Cargando datos de 1025 Pokémon... (Ten paciencia, esto tarda unos 30s)")

    val respuestaLista = cliente.send(peticionLista, HttpResponse.BodyHandlers.ofString())

    if (respuestaLista.statusCode() == 200) {
        val listaData = jsonConfig.decodeFromString<PokemonListResponse>(respuestaLista.body())

        // Descargamos los detalles uno por uno
        val pokemonDetallados = listaData.results.mapIndexed { index, p ->
            if (index % 50 == 0) println("Cargando... $index/1025") // Feedback para que no desesperes
            val req = HttpRequest.newBuilder().uri(URI.create(p.url)).GET().build()
            val res = cliente.send(req, HttpResponse.BodyHandlers.ofString())
            jsonConfig.decodeFromString<PokemonDetail>(res.body())
        }

        println("¡Pokédex Lista! Todos los datos descargados.")

        println("\n--- MENU DE CONSULTAS ---")
        println("1. Filtrar por PESO")
        println("2. Filtrar por TIPO")
        println("3. Filtrar por GENERACIÓN")
        print("Selecciona (1-3): ")

        when (readln()) {
            "1" -> {
                print("Peso mínimo en KG: ")
                val pesoMin = readln().toDoubleOrNull() ?: 0.0
                val filtrados = pokemonDetallados.filter { (it.weight / 10.0) >= pesoMin }
                println("\n RESULTADOS (Peso >= $pesoMin kg):")
                filtrados.forEach { println("- ${it.name.uppercase()} (${it.weight / 10.0} kg)") }
            }
            "2" -> {
                print("Tipo (ej: fire, water, ghost): ")
                val tipo = readln().lowercase()
                val filtrados = pokemonDetallados.filter { p -> p.types.any { it.type.name == tipo } }
                println("\n RESULTADOS (Tipo: $tipo):")
                filtrados.forEach { println("- ${it.name.uppercase()}") }
            }
            "3" -> {
                println("Generaciones: 1(Kanto), 2(Johto), 3(Hoenn), 4(Sinnoh), 5(Unova), 6(Kalos), 7(Alola), 8(Galar), 9(Paldea)")
                print("Introduce número (1-9): ")
                val gen = readln().toIntOrNull() ?: 1

                val filtrados = when(gen) {
                    1 -> pokemonDetallados.filter { it.id in 1..151 }
                    2 -> pokemonDetallados.filter { it.id in 152..251 }
                    3 -> pokemonDetallados.filter { it.id in 252..386 }
                    4 -> pokemonDetallados.filter { it.id in 387..493 }
                    5 -> pokemonDetallados.filter { it.id in 494..649 }
                    6 -> pokemonDetallados.filter { it.id in 650..721 }
                    7 -> pokemonDetallados.filter { it.id in 722..809 }
                    8 -> pokemonDetallados.filter { it.id in 810..905 }
                    9 -> pokemonDetallados.filter { it.id in 906..1025 }
                    else -> emptyList()
                }

                if (filtrados.isEmpty()) {
                    println(" No se encontraron Pokémon. Asegúrate de elegir entre 1 y 9.")
                } else {
                    val regiones = listOf("", "Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos", "Alola", "Galar", "Paldea")
                    println("\n POKÉMON DE LA GENERACIÓN $gen (Región ${regiones[gen]}):")
                    println(filtrados.joinToString(", ") { "${it.id}:${it.name.uppercase()}" })
                    println("\nTotal: ${filtrados.size} encontrados.")
                }
            }

            else -> println("Opción incorrecta.")
        }

    } else {
        println(" Error de red: ${respuestaLista.statusCode()}")
    }
}
