package com.naranjapina.heat_tourism.core.utils

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.company.data.model.Company
import com.naranjapina.heat_tourism.features.map.presentation.model.MapPoint
import com.naranjapina.heat_tourism.features.route.data.model.Route
import com.naranjapina.heat_tourism.features.social.data.model.Post
import com.naranjapina.heat_tourism.features.travel.data.model.GrupoViaje
import kotlinx.coroutines.tasks.await

object FirestoreSeeder {
    private val db = Firebase.firestore

    suspend fun seedAll() {
        seedDestinations()
        seedRoutes()
        seedCompanies()
        seedPosts()
        seedTravelGroups()
    }

    private suspend fun seedDestinations() {
        val destinations = listOf(
            MapPoint("1", "Parque Explora", "Centro interactivo de ciencia y tecnología.", -75.564, 6.270, "museum"),
            MapPoint("2", "Jardín Botánico", "Gran variedad de flora tropical y un orquideorama.", -75.562, 6.272, "nature"),
            MapPoint("3", "Museo de Antioquia", "Obras de Fernando Botero y otros artistas.", -75.568, 6.252, "culture")
        )
        for (d in destinations) {
            db.collection("destinations").document(d.id).set(d).await()
        }
        Log.d("Seeder", "Destinations seeded")
    }

    private suspend fun seedRoutes() {
        val routes = listOf(
            Route("r1", "Ruta de la Innovación", "Conoce los puntos más modernos de Medellín.", "4h", "$50.000", listOf("1", "2")),
            Route("r2", "Ruta Cultural", "Explora la historia y el arte local.", "3h", "$35.000", listOf("3"))
        )
        for (r in routes) {
            db.collection("routes").document(r.id).set(r).await()
        }
        Log.d("Seeder", "Routes seeded")
    }

    private suspend fun seedCompanies() {
        val companies = listOf(
            Company("c1", "Medellín Tours", null, "Expertos en la ciudad.", "info@medellintours.com", "1234567", listOf("r1"), emptyList(), 4.8),
            Company("c2", "Arte y Cultura", null, "Descubre el alma de Medellín.", "contacto@arte.com", "7654321", listOf("r2"), emptyList(), 4.5)
        )
        for (c in companies) {
            db.collection("companies").document(c.id!!).set(c).await()
        }
        Log.d("Seeder", "Companies seeded")
    }

    private suspend fun seedPosts() {
        val posts = listOf(
            Post("p1", "u1", "Juan Perez", null, "Increíble vista en el Parque Explora!", null, "Parque Explora"),
            Post("p2", "u2", "Maria Lopez", null, "Las orquídeas están hermosas hoy.", null, "Jardín Botánico")
        )
        for (p in posts) {
            db.collection("posts").document(p.id!!).set(p).await()
        }
        Log.d("Seeder", "Posts seeded")
    }

    private suspend fun seedTravelGroups() {
        val groups = listOf(
            GrupoViaje("g1", "r1", "Ruta de la Innovación", System.currentTimeMillis(), "ACTIVE"),
            GrupoViaje("g2", "r2", "Ruta Cultural", System.currentTimeMillis() - 86400000, "COMPLETED")
        )
        for (g in groups) {
            db.collection("travel_groups").document(g.id!!).set(g).await()
        }
        Log.d("Seeder", "Travel Groups seeded")
    }
}
