package com.naranjapina.heat_tourism.data.model.social

import com.google.firebase.Timestamp

data class Chat(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val participantPhotos: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val lastSenderId: String = "",
    val lastMessageAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

