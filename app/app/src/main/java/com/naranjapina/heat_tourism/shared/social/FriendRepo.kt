package com.naranjapina.heat_tourism.shared.social

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.naranjapina.heat_tourism.data.model.social.Amistad
import com.naranjapina.heat_tourism.data.model.social.EstadoAmistad
import com.naranjapina.heat_tourism.data.model.social.TipoNotificacion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class UserSearchItem(
    val id: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?
)

data class FriendListItem(
    val friendshipId: String,
    val userId: String,
    val displayName: String,
    val photoUrl: String?
)

data class FriendRequestItem(
    val friendshipId: String,
    val fromUserId: String,
    val fromDisplayName: String,
    val fromPhotoUrl: String?
)

class FriendRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val users = db.collection("users")
    private val friendships = db.collection("friendships")
    private val notificationRepo = NotificationRepo()

    fun observeFriends(currentUserId: String): Flow<List<FriendListItem>> = callbackFlow {
        val registration = friendships
            .whereArrayContains("participantIds", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents
                    ?.map { doc -> doc.toAmistad() }
                    ?.filter { it.estado == EstadoAmistad.ACEPTADA }
                    ?.mapNotNull { amistad ->
                        val isRequester = amistad.requesterId == currentUserId
                        if (isRequester) {
                            FriendListItem(
                                friendshipId = amistad.id,
                                userId = amistad.addresseeId,
                                displayName = amistad.addresseeName,
                                photoUrl = amistad.addresseePhotoUrl
                            )
                        } else if (amistad.addresseeId == currentUserId) {
                            FriendListItem(
                                friendshipId = amistad.id,
                                userId = amistad.requesterId,
                                displayName = amistad.requesterName,
                                photoUrl = amistad.requesterPhotoUrl
                            )
                        } else {
                            null
                        }
                    }
                    ?.sortedBy { it.displayName.lowercase() }
                    ?: emptyList()

                trySend(items)
            }

        awaitClose { registration.remove() }
    }

    fun observePendingRequests(currentUserId: String): Flow<List<FriendRequestItem>> = callbackFlow {
        val registration = friendships
            .whereEqualTo("addresseeId", currentUserId)
            .whereEqualTo("estado", EstadoAmistad.PENDIENTE.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents
                    ?.map { doc -> doc.toAmistad() }
                    ?.map { amistad ->
                        FriendRequestItem(
                            friendshipId = amistad.id,
                            fromUserId = amistad.requesterId,
                            fromDisplayName = amistad.requesterName,
                            fromPhotoUrl = amistad.requesterPhotoUrl
                        )
                    }
                    ?.sortedByDescending { it.fromDisplayName.lowercase() }
                    ?: emptyList()

                trySend(requests)
            }

        awaitClose { registration.remove() }
    }

    suspend fun searchUsers(query: String, currentUserId: String): List<UserSearchItem> {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return emptyList()

        val nameResult = users
            .orderBy("searchName")
            .startAt(normalized)
            .endAt("$normalized\uf8ff")
            .limit(20)
            .get()
            .await()

        val emailResult = users
            .orderBy("searchEmail")
            .startAt(normalized)
            .endAt("$normalized\uf8ff")
            .limit(20)
            .get()
            .await()

        val legacyEmailResult = users
            .orderBy("email")
            .startAt(normalized)
            .endAt("$normalized\uf8ff")
            .limit(20)
            .get()
            .await()

        val merged = (nameResult.documents + emailResult.documents + legacyEmailResult.documents)
            .associateBy { it.id }
            .values

        return merged.mapNotNull { doc ->
            // Usar authId (Firebase Auth uid) como identificador, no el doc.id auto-generado.
            // Esto asegura consistencia con FirebaseAuth.currentUser?.uid que usan las pantallas.
            val authId = doc.getString("authId") ?: doc.id
            if (authId == currentUserId) return@mapNotNull null
            UserSearchItem(
                id = authId,
                displayName = doc.getString("displayName").orEmpty()
                    .ifBlank { doc.getString("fullName").orEmpty() }
                    .ifBlank { "Usuario" },
                email = doc.getString("email").orEmpty(),
                photoUrl = doc.getString("photoUrl")
                    ?: doc.getString("avatarURL")
            )
        }.sortedBy { it.displayName.lowercase() }
    }

    /** Busca un user doc por su authId (Firebase Auth uid). */
    private suspend fun findUserDocByAuthId(authId: String) =
        users.whereEqualTo("authId", authId).limit(1).get().await().documents.firstOrNull()

    suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Boolean {
        if (fromUserId == toUserId) return false

        val friendshipId = friendshipId(fromUserId, toUserId)
        val docRef = friendships.document(friendshipId)
        val existing = docRef.get().await()
        val existingStatus = existing.getString("estado")

        if (existingStatus == EstadoAmistad.PENDIENTE.name || existingStatus == EstadoAmistad.ACEPTADA.name) {
            return false
        }

        // Buscar profiles por authId (Firebase Auth uid), no por doc.id
        val fromProfile = findUserDocByAuthId(fromUserId)
        val toProfile = findUserDocByAuthId(toUserId)

        val fromName = (fromProfile?.getString("displayName")
            ?: fromProfile?.getString("fullName"))
            .orEmpty().ifBlank { "Usuario" }
        val toName = (toProfile?.getString("displayName")
            ?: toProfile?.getString("fullName"))
            .orEmpty().ifBlank { "Usuario" }
        val fromPhoto = fromProfile?.getString("photoUrl")
            ?: fromProfile?.getString("avatarURL")
        val toPhoto = toProfile?.getString("photoUrl")
            ?: toProfile?.getString("avatarURL")

        docRef.set(
            mapOf(
                "requesterId" to fromUserId,
                "requesterName" to fromName,
                "requesterPhotoUrl" to fromPhoto,
                "addresseeId" to toUserId,
                "addresseeName" to toName,
                "addresseePhotoUrl" to toPhoto,
                "participantIds" to listOf(fromUserId, toUserId),
                "estado" to EstadoAmistad.PENDIENTE.name,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        notificationRepo.createNotification(
            userId = toUserId,
            fromUserId = fromUserId,
            tipo = TipoNotificacion.SOLICITUD_AMISTAD,
            titulo = "Nueva solicitud de amistad",
            cuerpo = "$fromName te envio una solicitud",
            amistadId = friendshipId
        )
        return true
    }

    suspend fun acceptRequest(friendshipId: String, currentUserId: String) {
        val docRef = friendships.document(friendshipId)
        val snapshot = docRef.get().await()
        val requesterId = snapshot.getString("requesterId").orEmpty()
        val accepterName = snapshot.getString("addresseeName").orEmpty().ifBlank { "Alguien" }

        docRef.update(
            mapOf(
                "estado" to EstadoAmistad.ACEPTADA.name,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        if (requesterId.isNotBlank() && requesterId != currentUserId) {
            notificationRepo.createNotification(
                userId = requesterId,
                fromUserId = currentUserId,
                tipo = TipoNotificacion.SOLICITUD_AMISTAD,
                titulo = "Solicitud aceptada",
                cuerpo = "$accepterName acepto tu solicitud",
                amistadId = friendshipId
            )
        }
    }

    suspend fun rejectRequest(friendshipId: String) {
        friendships.document(friendshipId).update(
            mapOf(
                "estado" to EstadoAmistad.RECHAZADA.name,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun removeFriend(friendshipId: String) {
        friendships.document(friendshipId).delete().await()
    }

    private fun friendshipId(userA: String, userB: String): String =
        listOf(userA, userB).sorted().joinToString("_")

    private fun com.google.firebase.firestore.DocumentSnapshot.toAmistad(): Amistad {
        return Amistad(
            id = id,
            requesterId = getString("requesterId").orEmpty(),
            requesterName = getString("requesterName").orEmpty().ifBlank { "Usuario" },
            requesterPhotoUrl = getString("requesterPhotoUrl"),
            addresseeId = getString("addresseeId").orEmpty(),
            addresseeName = getString("addresseeName").orEmpty().ifBlank { "Usuario" },
            addresseePhotoUrl = getString("addresseePhotoUrl"),
            participantIds = (get("participantIds") as? List<*>)?.filterIsInstance<String>().orEmpty(),
            estado = runCatching {
                EstadoAmistad.valueOf(getString("estado").orEmpty())
            }.getOrDefault(EstadoAmistad.PENDIENTE),
            createdAt = getTimestamp("createdAt"),
            updatedAt = getTimestamp("updatedAt")
        )
    }
}

