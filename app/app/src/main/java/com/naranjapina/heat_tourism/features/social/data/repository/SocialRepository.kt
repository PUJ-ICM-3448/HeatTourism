package com.naranjapina.heat_tourism.features.social.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.social.data.model.Comment
import com.naranjapina.heat_tourism.features.social.data.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SocialRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val postsCollection = db.collection("posts")

    suspend fun getGlobalPosts(): List<Post> {
        return try {
            val snapshot = postsCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
            snapshot.toObjects(Post::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
// ... código existente ...

    /**
     * Obtiene una publicación en tiempo real con un SnapshotListener.
     */
    fun getPostFlow(postId: String): Flow<Post?> = callbackFlow {
        val listener = postsCollection.document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val post = snapshot.toObject(Post::class.java)
                    trySend(post)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Obtiene los comentarios de una publicación en tiempo real con SnapshotListener.
     */
    fun getCommentsFlow(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = postsCollection.document(postId)
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.toObjects(Comment::class.java)
                    trySend(comments)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Agrega un comentario en la subcolección "comments" bajo el documento de la publicación.
     */
    suspend fun addComment(postId: String, comment: Comment) {
        val payload = hashMapOf(
            "userId" to comment.userId,
            "userName" to comment.userName,
            "userAvatar" to comment.userAvatar,
            "text" to comment.text,
            "timestamp" to comment.timestamp
        )
        postsCollection.document(postId)
            .collection("comments")
            .add(payload)
            .await()
    }

    /**
     * Alterna la reacción (like) de un usuario en una publicación mediante una transacción.
     */
    // ... código existente ...
        suspend fun toggleLike(postId: String, userId: String) {
            val docRef = postsCollection.document(postId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val likes = (snapshot.get("likes") as? List<*>)?.map { it.toString() } ?: emptyList()
                val newLikes = if (likes.contains(userId)) {
                    likes - userId
                } else {
                    likes + userId
                }
                transaction.update(docRef, "likes", newLikes)
                transaction.update(docRef, "likesCount", newLikes.size)
            }.await()
        }
    }
