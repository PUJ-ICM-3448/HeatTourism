package com.naranjapina.heat_tourism.shared.social

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.naranjapina.heat_tourism.data.model.social.Notificacion
import com.naranjapina.heat_tourism.data.model.social.TipoNotificacion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class NotificationRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val notifications = db.collection("notifications")
    private val users = db.collection("users")

    fun observeNotifications(userId: String): Flow<List<Notificacion>> = callbackFlow {
        val registration = notifications
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents
                    ?.map { doc ->
                        Notificacion(
                            id = doc.id,
                            userId = doc.getString("userId").orEmpty(),
                            fromUserId = doc.getString("fromUserId"),
                            tipo = runCatching {
                                TipoNotificacion.valueOf(
                                    doc.getString("tipo").orEmpty()
                                )
                            }.getOrDefault(TipoNotificacion.CHAT),
                            titulo = doc.getString("titulo").orEmpty(),
                            cuerpo = doc.getString("cuerpo").orEmpty(),
                            chatId = doc.getString("chatId"),
                            amistadId = doc.getString("amistadId"),
                            leida = doc.getBoolean("leida") ?: false,
                            createdAt = doc.getTimestamp("createdAt")
                        )
                    }
                    ?.sortedByDescending { it.createdAt ?: Timestamp.now() }
                    ?: emptyList()

                trySend(list)
            }

        awaitClose { registration.remove() }
    }

    fun observeUnreadCount(userId: String): Flow<Int> =
        observeNotifications(userId).map { notificationsList ->
            notificationsList.count { !it.leida }
        }

    suspend fun createNotification(
        userId: String,
        fromUserId: String? = null,
        tipo: TipoNotificacion,
        titulo: String,
        cuerpo: String,
        chatId: String? = null,
        amistadId: String? = null
    ) {
        notifications.add(
            mapOf(
                "userId" to userId,
                "fromUserId" to fromUserId,
                "tipo" to tipo.name,
                "titulo" to titulo,
                "cuerpo" to cuerpo,
                "chatId" to chatId,
                "amistadId" to amistadId,
                "leida" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun createCoordinatorAlert(
        userId: String,
        fromUserId: String? = null,
        titulo: String,
        cuerpo: String
    ) {
        createNotification(
            userId = userId,
            fromUserId = fromUserId,
            tipo = TipoNotificacion.ALERTA_COORDINADOR,
            titulo = titulo,
            cuerpo = cuerpo
        )
    }

    suspend fun markAsRead(notificationId: String) {
        notifications.document(notificationId)
            .update("leida", true)
            .await()
    }

    suspend fun markAllAsRead(userId: String) {
        val unread = notifications
            .whereEqualTo("userId", userId)
            .whereEqualTo("leida", false)
            .get()
            .await()

        if (unread.isEmpty) return

        val batch = db.batch()
        unread.documents.forEach { doc ->
            batch.update(doc.reference, "leida", true)
        }
        batch.commit().await()
    }

    suspend fun markChatNotificationsAsRead(userId: String, chatId: String) {
        val unread = notifications
            .whereEqualTo("userId", userId)
            .whereEqualTo("chatId", chatId)
            .whereEqualTo("leida", false)
            .get()
            .await()

        if (unread.isEmpty) return

        val batch = db.batch()
        unread.documents.forEach { doc ->
            batch.update(doc.reference, "leida", true)
        }
        batch.commit().await()
    }

    fun syncFcmTokenForCurrentUser() {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            // Buscar el user doc por authId (no asumir doc.id == authId)
            users.whereEqualTo("authId", uid).limit(1).get()
                .addOnSuccessListener { snapshot ->
                    val docRef = snapshot.documents.firstOrNull()?.reference
                        ?: return@addOnSuccessListener
                    docRef.set(
                        mapOf(
                            "fcmTokens" to FieldValue.arrayUnion(token),
                            "updatedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                }
        }
    }
}

