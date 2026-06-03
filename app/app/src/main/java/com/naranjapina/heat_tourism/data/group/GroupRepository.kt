package com.naranjapina.heat_tourism.data.group

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class GroupRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    fun createGroup(groupId: String) {
        db.collection("groups")
            .document(groupId)
            .set(
                mapOf(
                    "createdAt" to System.currentTimeMillis(),
                    "name" to "Mi viaje"
                )
            )
    }

    fun addMemberToGroup(
        groupId: String,
        userId: String
    ) {
        db.collection("groups")
            .document(groupId)
            .collection("members")
            .document(userId)
            .set(
                mapOf(
                    "lat" to 0.0,
                    "lng" to 0.0,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
    }
}