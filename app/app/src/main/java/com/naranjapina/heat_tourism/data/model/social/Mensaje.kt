package com.naranjapina.heat_tourism.data.model.social

import com.google.firebase.Timestamp

data class Mensaje(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null
)

