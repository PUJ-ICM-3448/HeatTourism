package com.naranjapina.heat_tourism.data.service

import com.naranjapina.heat_tourism.data.model.RouteSummary
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Wrapper minimal sobre Mapbox Directions API (Bloque B).
 *
 * Endpoint:
 *   https://api.mapbox.com/directions/v5/{profile}/{lng1},{lat1};{lng2},{lat2}
 *
 * Devuelve una RouteSummary con distancia, duracion y la geometria
 * (decodificada como lista de pares lng/lat).
 */
class MapboxDirectionsService(private val accessToken: String) {

  private val json = Json { ignoreUnknownKeys = true; isLenient = true }

  private val client = HttpClient(CIO) {
    install(ContentNegotiation) { json(json) }
  }

  /**
   * Calcula la ruta entre dos puntos usando el perfil dado.
   * @param profile "driving", "walking", "cycling" o "driving-traffic".
   */
  suspend fun getRoute(
    startLng: Double,
    startLat: Double,
    endLng: Double,
    endLat: Double,
    profile: String = "walking"
  ): Result<RouteSummary> = withContext(Dispatchers.IO) {
    runCatching {
      val url = "https://api.mapbox.com/directions/v5/mapbox/$profile/" +
          "$startLng,$startLat;$endLng,$endLat" +
          "?geometries=geojson&overview=full&access_token=$accessToken"

      val raw = client.get(url).bodyAsText()
      val root = json.parseToJsonElement(raw).jsonObject

      val routes = root["routes"]?.jsonArray
        ?: throw IllegalStateException("Mapbox: respuesta sin 'routes'")
      if (routes.isEmpty()) throw IllegalStateException("Mapbox: lista de rutas vacia")

      val first = routes[0].jsonObject
      val distance = first["distance"]?.jsonPrimitive?.doubleOrNull
        ?: throw IllegalStateException("Mapbox: ruta sin 'distance'")
      val duration = first["duration"]?.jsonPrimitive?.doubleOrNull
        ?: throw IllegalStateException("Mapbox: ruta sin 'duration'")

      val coords = first["geometry"]?.jsonObject
        ?.get("coordinates")?.jsonArray
        ?: throw IllegalStateException("Mapbox: geometria invalida")

      val geometry = coords.map { node ->
        val arr = node.jsonArray
        val lng = arr[0].jsonPrimitive.doubleOrNull ?: 0.0
        val lat = arr[1].jsonPrimitive.doubleOrNull ?: 0.0
        lng to lat
      }

      RouteSummary(
        distanceMeters = distance,
        durationSeconds = duration,
        geometry = geometry
      )
    }
  }

  fun close() = client.close()
}
