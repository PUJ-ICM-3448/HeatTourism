package com.naranjapina.heat_tourism.features.social.presentation.Chats

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.shared.social.ChatRepo
import com.naranjapina.heat_tourism.shared.social.NotificationRepo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    navController: NavHostController,
    otherUserId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val chatRepo = remember { ChatRepo() }
    val notificationRepo = remember { NotificationRepo() }
    val chatId = remember(currentUserId, otherUserId) {
        if (currentUserId == null) "" else chatRepo.buildChatId(currentUserId, otherUserId)
    }

    var input by remember { mutableStateOf("") }
    val messages by remember(currentUserId, otherUserId) {
        if (currentUserId == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else chatRepo.observeMessages(currentUserId, otherUserId)
    }.collectAsState(initial = emptyList())

    LaunchedEffect(currentUserId, chatId) {
        if (currentUserId != null && chatId.isNotBlank()) {
            notificationRepo.markChatNotificationsAsRead(currentUserId, chatId)
        }
    }

    MenuBottonLayout(activeName = "chats", navController = navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.beige))
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 15.dp)
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Text(
                text = "Chat",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = "Conversacion con: $otherUserId",
                color = colorResource(R.color.dark_beige),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    val mine = message.senderId == currentUserId
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = if (mine) colorResource(R.color.red_100) else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(text = message.text)
                            Text(
                                text = message.createdAt?.toDate()?.toShortTime().orEmpty(),
                                color = colorResource(R.color.dark_beige),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Escribe un mensaje") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Enviar"
            ) {
                val uid = currentUserId
                if (uid == null) {
                    Toast.makeText(context, "Debes iniciar sesion", Toast.LENGTH_SHORT).show()
                    return@GradientButton
                }
                scope.launch {
                    val sent = chatRepo.sendMessage(uid, otherUserId, input)
                    if (sent) {
                        input = ""
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

