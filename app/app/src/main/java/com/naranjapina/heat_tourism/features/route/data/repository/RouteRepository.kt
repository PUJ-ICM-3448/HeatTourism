package com.naranjapina.heat_tourism.features.route.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.route.data.model.Route
import kotlinx.coroutines.tasks.await

import com.naranjapina.heat_tourism.core.component.DestinationCardData
import com.naranjapina.heat_tourism.features.social.data.model.Post
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class RouteRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val routesCollection = db.collection("routes")

    suspend fun getPopularRoutes(): List<DestinationCardData> {
        return try {
            val snapshot = routesCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            snapshot.toObjects(Route::class.java).map { route ->
                DestinationCardData(
                    imgUrl = "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=1200", // Fallback URL
                    contentDescription = route.description,
                    destinationScore = 4.5f, // Valor por defecto
                    destinationName = route.name
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRouteById(id: String): Route? {
        return try {
            routesCollection.document(id).get().await().toObject(Route::class.java)
        } catch (e: Exception) {
            null
        }
    }
// ... código existente ...

    suspend fun saveRoute(route: Route) {
        val payload = hashMapOf(
            "name" to route.name,
            "description" to route.description,
            "duration" to route.duration,
            "price" to route.price,
            "stops" to route.stops,
            "timestamp" to route.timestamp
        )
        routesCollection.add(payload).await()
    }
}
