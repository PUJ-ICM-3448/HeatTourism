package com.naranjapina.heat_tourism.features.social.presentation.Friend

import android.widget.Toast
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
import androidx.compose.material3.Button
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
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.shared.social.FriendRepo
import com.naranjapina.heat_tourism.shared.social.UserSearchItem
import kotlinx.coroutines.launch

@Composable
fun FriendScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    val friendRepo = remember { FriendRepo() }

    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf(emptyList<UserSearchItem>()) }

    val friends by remember(currentUid) {
        if (currentUid == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else friendRepo.observeFriends(currentUid)
    }.collectAsState(initial = emptyList())

    val pendingRequests by remember(currentUid) {
        if (currentUid == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else friendRepo.observePendingRequests(currentUid)
    }.collectAsState(initial = emptyList())

    MenuBottonLayout(activeName = "friends", navController = navController) { padding ->
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
                    text = "Amigos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar usuarios") },
                    placeholder = { Text("Escribe nombre o email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (searching) "Buscando..." else "Buscar"
                ) {
                    if (currentUid == null) {
                        Toast.makeText(context, "Debes iniciar sesion", Toast.LENGTH_SHORT).show()
                        return@GradientButton
                    }
                    scope.launch {
                        searching = true
                        searchResults = friendRepo.searchUsers(query, currentUid)
                        searching = false
                    }
                }
            }

            if (searchResults.isNotEmpty()) {
                item {
                    Text(
                        text = "Resultados",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
                items(searchResults, key = { it.id }) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = user.displayName, fontWeight = FontWeight.SemiBold)
                            if (user.email.isNotBlank()) {
                                Text(
                                    text = user.email,
                                    color = colorResource(R.color.dark_beige),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Button(
                            onClick = {
                                val uid = currentUid ?: return@Button
                                scope.launch {
                                    val sent = friendRepo.sendFriendRequest(uid, user.id)
                                    val message = if (sent) {
                                        "Solicitud enviada a ${user.displayName}"
                                    } else {
                                        "Ya existe una relacion con ${user.displayName}"
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Agregar")
                        }
                    }
                }
            }

            if (pendingRequests.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Solicitudes recibidas",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
                items(pendingRequests, key = { it.friendshipId }) { request ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = request.fromDisplayName, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val uid = currentUid ?: return@Button
                                    scope.launch {
                                        friendRepo.acceptRequest(request.friendshipId, uid)
                                    }
                                }
                            ) {
                                Text("Aceptar")
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        friendRepo.rejectRequest(request.friendshipId)
                                    }
                                }
                            ) {
                                Text("Rechazar")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mis amigos",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            if (friends.isEmpty()) {
                item {
                    Text(
                        text = "Aun no tienes amigos agregados",
                        color = colorResource(R.color.dark_beige)
                    )
                }
            } else {
                items(friends, key = { it.friendshipId }) { friend ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = friend.displayName, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Abrir chat",
                                color = colorResource(R.color.red_400),
                                modifier = Modifier.clickable {
                                    navController.navigate("${Screen.Chat.name}/${friend.userId}")
                                }
                            )
                            Text(
                                text = "Eliminar",
                                color = colorResource(R.color.red_500),
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        friendRepo.removeFriend(friend.friendshipId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

