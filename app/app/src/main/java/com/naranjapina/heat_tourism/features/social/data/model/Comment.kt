package com.naranjapina.heat_tourism.features.social.data.model

import com.google.firebase.firestore.DocumentId

data class Comment(
    @DocumentId val id: String? = null,
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String? = null,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
