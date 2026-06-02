package com.naranjapina.heat_tourism.features.social.data.model

import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId val id: String? = null,
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String? = null,
    val description: String = "",
    val imageUrl: String? = null,
    val location: String? = null,
    val likesCount: Int = 0,
    val likes: List<String> = emptyList(), // user IDs who liked
    val timestamp: Long = System.currentTimeMillis()
)
