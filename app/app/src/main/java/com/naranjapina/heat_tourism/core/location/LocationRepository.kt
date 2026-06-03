package com.naranjapina.heat_tourism.features.location.data

import com.google.firebase.firestore.FirebaseFirestore
import com.naranjapina.heat_tourism.features.location.model.LocationModel

class LocationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun updateLocation(groupId: String, userId: String, lat: Double, lng: Double) {
        val data = mapOf(
            "lat" to lat,
            "lng" to lng,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection("groups")
            .document(groupId)
            .collection("members")
            .document(userId)
            .set(data)
    }

    fun listenGroupLocations(
        groupId: String,
        onUpdate: (List<LocationModel>) -> Unit
    ) {
        db.collection("groups")
            .document(groupId)
            .collection("members")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.map {
                    LocationModel(
                        lat = it.getDouble("lat") ?: 0.0,
                        lng = it.getDouble("lng") ?: 0.0,
                        userId = it.id
                    )
                } ?: emptyList()
                onUpdate(list)
            }
    }
}
