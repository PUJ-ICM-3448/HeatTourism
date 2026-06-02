package com.naranjapina.heat_tourism.features.social.presentation.Notifications

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.shared.social.NotificationRepo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    val notificationRepo = remember { NotificationRepo() }

    val notifications by remember(currentUid) {
        if (currentUid == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else notificationRepo.observeNotifications(currentUid)
    }.collectAsState(initial = emptyList())

    MenuBottonLayout(activeName = "notifications", navController = navController) { padding ->
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "Marcar todo leido",
                        color = colorResource(R.color.red_400),
                        modifier = Modifier.clickable {
                            val uid = currentUid ?: return@clickable
                            scope.launch {
                                notificationRepo.markAllAsRead(uid)
                            }
                        }
                    )
                }
            }

            if (notifications.isEmpty()) {
                item {
                    Text(
                        text = "No hay notificaciones",
                        color = colorResource(R.color.dark_beige)
                    )
                }
            } else {
                items(notifications, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (item.leida) Color.White else colorResource(R.color.red_50),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                scope.launch {
                                    notificationRepo.markAsRead(item.id)
                                }
                                if (item.chatId != null && item.fromUserId != null) {
                                    navController.navigate("${Screen.Chat.name}/${item.fromUserId}")
                                }
                            }
                            .padding(12.dp)
                    ) {
                        Text(text = item.titulo, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = item.cuerpo,
                            color = colorResource(R.color.dark_beige),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = item.createdAt?.toDate()?.toLongDate().orEmpty(),
                            color = colorResource(R.color.dark_beige),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun Date.toLongDate(): String {
    val format = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    return format.format(this)
}

