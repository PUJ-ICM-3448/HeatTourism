package com.naranjapina.heat_tourism.features.social.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.map.presentation.model.MapPoint
import kotlinx.coroutines.tasks.await

class DestinationRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val destinationsCollection = db.collection("destinations")

    suspend fun getDestinationsByQuery(query: String, category: String): List<MapPoint> {
        return try {
            val snapshot = if (category != "all") {
                destinationsCollection.whereEqualTo("category", category).get().await()
            } else {
                destinationsCollection.get().await()
            }

            val destinations = snapshot.toObjects(MapPoint::class.java)

            if (query.isBlank()) {
                destinations
            } else {
                val lowerQuery = query.lowercase()
                destinations.filter {
                    it.name.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDestinationById(id: String): MapPoint? {
        return try {
            destinationsCollection.document(id).get().await().toObject(MapPoint::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateDestination(destination: MapPoint): Boolean {
        return try {
            destinationsCollection.document(destination.id).set(destination).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
