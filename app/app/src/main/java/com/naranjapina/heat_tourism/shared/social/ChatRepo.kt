package com.naranjapina.heat_tourism.shared.social

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.naranjapina.heat_tourism.data.model.social.Chat
import com.naranjapina.heat_tourism.data.model.social.Mensaje
import com.naranjapina.heat_tourism.data.model.social.TipoNotificacion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class ChatPreviewItem(
    val chatId: String,
    val otherUserId: String,
    val otherDisplayName: String,
    val otherPhotoUrl: String?,
    val lastMessage: String,
    val lastMessageAt: Timestamp?
)

class ChatRepo {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val users = db.collection("users")
    private val chats = db.collection("chats")
    private val notificationRepo = NotificationRepo()

    fun observeChats(currentUserId: String): Flow<List<ChatPreviewItem>> = callbackFlow {
        val registration = chats
            .whereArrayContains("participantIds", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val previews = snapshot?.documents
                    ?.map { doc -> doc.toChat() }
                    ?.mapNotNull { chat ->
                        val otherUserId = chat.participantIds.firstOrNull { it != currentUserId } ?: return@mapNotNull null
                        ChatPreviewItem(
                            chatId = chat.id,
                            otherUserId = otherUserId,
                            otherDisplayName = chat.participantNames[otherUserId].orEmpty().ifBlank { "Usuario" },
                            otherPhotoUrl = chat.participantPhotos[otherUserId],
                            lastMessage = chat.lastMessage,
                            lastMessageAt = chat.lastMessageAt
                        )
                    }
                    ?.sortedByDescending { it.lastMessageAt ?: Timestamp.now() }
                    ?: emptyList()

                trySend(previews)
            }

        awaitClose { registration.remove() }
    }

    fun observeMessages(currentUserId: String, otherUserId: String): Flow<List<Mensaje>> = callbackFlow {
        val chatId = buildChatId(currentUserId, otherUserId)
        val registration = chats.document(chatId)
            .collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents
                    ?.map { doc ->
                        Mensaje(
                            id = doc.id,
                            chatId = chatId,
                            senderId = doc.getString("senderId").orEmpty(),
                            receiverId = doc.getString("receiverId").orEmpty(),
                            text = doc.getString("text").orEmpty(),
                            createdAt = doc.getTimestamp("createdAt")
                        )
                    }
                    ?: emptyList()

                trySend(messages)
            }

        awaitClose { registration.remove() }
    }

    suspend fun sendMessage(currentUserId: String, otherUserId: String, text: String): Boolean {
        val messageText = text.trim()
        if (messageText.isBlank()) return false

        val chatId = buildChatId(currentUserId, otherUserId)
        val chatRef = chats.document(chatId)
        val messageRef = chatRef.collection("messages").document()

        val currentUserDoc = users.document(currentUserId).get().await()
        val otherUserDoc = users.document(otherUserId).get().await()

        val currentName = currentUserDoc.getString("displayName").orEmpty().ifBlank { "Usuario" }
        val otherName = otherUserDoc.getString("displayName").orEmpty().ifBlank { "Usuario" }

        val participantNames = mapOf(
            currentUserId to currentName,
            otherUserId to otherName
        )
        val participantPhotos = mapOf(
            currentUserId to currentUserDoc.getString("photoUrl").orEmpty(),
            otherUserId to otherUserDoc.getString("photoUrl").orEmpty()
        )

        val batch = db.batch()
        batch.set(
            chatRef,
            mapOf(
                "participantIds" to listOf(currentUserId, otherUserId),
                "participantNames" to participantNames,
                "participantPhotos" to participantPhotos,
                "lastMessage" to messageText,
                "lastSenderId" to currentUserId,
                "lastMessageAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )

        batch.set(
            messageRef,
            mapOf(
                "chatId" to chatId,
                "senderId" to currentUserId,
                "receiverId" to otherUserId,
                "text" to messageText,
                "createdAt" to FieldValue.serverTimestamp()
            )
        )
        batch.commit().await()

        notificationRepo.createNotification(
            userId = otherUserId,
            fromUserId = currentUserId,
            tipo = TipoNotificacion.CHAT,
            titulo = "Nuevo mensaje de $currentName",
            cuerpo = messageText.take(120),
            chatId = chatId
        )
        return true
    }

    fun buildChatId(currentUserId: String, otherUserId: String): String =
        listOf(currentUserId, otherUserId).sorted().joinToString("_")

    private fun com.google.firebase.firestore.DocumentSnapshot.toChat(): Chat {
        val names = (get("participantNames") as? Map<*, *>)
            ?.entries
            ?.associate { it.key.toString() to it.value.toString() }
            .orEmpty()

        val photos = (get("participantPhotos") as? Map<*, *>)
            ?.entries
            ?.associate { it.key.toString() to it.value.toString() }
            .orEmpty()

        return Chat(
            id = id,
            participantIds = (get("participantIds") as? List<*>)?.filterIsInstance<String>().orEmpty(),
            participantNames = names,
            participantPhotos = photos,
            lastMessage = getString("lastMessage").orEmpty(),
            lastSenderId = getString("lastSenderId").orEmpty(),
            lastMessageAt = getTimestamp("lastMessageAt"),
            updatedAt = getTimestamp("updatedAt")
        )
    }
}

