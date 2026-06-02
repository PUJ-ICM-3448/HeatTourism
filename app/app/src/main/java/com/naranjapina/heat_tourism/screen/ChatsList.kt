package com.naranjapina.heat_tourism.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.shared.social.ChatRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatsListScreen(navController: NavHostController) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    val chatRepo = remember { ChatRepo() }

    val chats by remember(currentUid) {
        if (currentUid == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else chatRepo.observeChats(currentUid)
    }.collectAsState(initial = emptyList())

    MenuBottonLayout(activeName = "chats", navController = navController) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .background(colorResource(R.color.beige))
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(padding.calculateTopPadding()))
                Text(
                    text = "Chats",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }

            if (chats.isEmpty()) {
                item {
                    Text(
                        text = "No tienes conversaciones aun.",
                        color = colorResource(R.color.dark_beige)
                    )
                }
            } else {
                items(chats, key = { it.chatId }) { chat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .clickable {
                                navController.navigate("${Screen.Chat.name}/${chat.otherUserId}")
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chat.otherDisplayName,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = chat.lastMessage.ifBlank { "Sin mensajes" },
                                color = colorResource(R.color.dark_beige),
                                maxLines = 1
                            )
                        }
                        Text(
                            text = chat.lastMessageAt?.toDate()?.toShortTime().orEmpty(),
                            color = colorResource(R.color.dark_beige),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun Date.toShortTime(): String {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(this)
}

