package com.naranjapina.heat_tourism.features.social.presentation.Post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    postId: String = "default_post_123",
    viewModel: PostViewModel = viewModel()
) {
    val authState by authViewModel.state.collectAsState()
    val currentUser = authState.user

    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var commentText by remember { mutableStateOf("") }

    // Cargar la publicación y escuchar en tiempo real al iniciar o cambiar de ID
    LaunchedEffect(postId) {
        viewModel.loadPostAndComments(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de Publicación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.previousBackStackEntry == null) {
                            navController.navigate(Screen.Home.name)
                        } else {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "Volver",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // Sección fija al final de la pantalla para ingresar nuevos comentarios
            Surface(
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorResource(R.color.beige).copy(0.4f),
                            unfocusedContainerColor = colorResource(R.color.beige).copy(0.4f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank() && currentUser != null) {
                                viewModel.addComment(
                                    text = commentText.trim(),
                                    userId = currentUser.id,
                                    userName = currentUser.fullName.ifEmpty { currentUser.userName ?: "Usuario" },
                                    userAvatar = currentUser.avatarURL
                                )
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank() && currentUser != null,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = colorResource(R.color.red_400),
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar Comentario"
                        )
                    }
                }
            }
        },
        containerColor = colorResource(R.color.beige)
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorResource(R.color.red_400))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                post?.let { currentPost ->
                    // Tarjeta Principal de la Publicación
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                colorResource(R.color.red_50),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!currentPost.userAvatar.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = currentPost.userAvatar,
                                                contentDescription = "Avatar",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = currentPost.userName.take(1).uppercase(),
                                                color = colorResource(R.color.red_400),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = currentPost.userName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = colorResource(R.color.red_400),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = currentPost.location ?: "Desconocido",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = formatTimestamp(currentPost.timestamp),
                                                fontSize = 11.sp,
                                                color = Color.LightGray
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = currentPost.description,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                if (!currentPost.imageUrl.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AsyncImage(
                                        model = currentPost.imageUrl,
                                        contentDescription = "Post Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                HorizontalDivider(color = colorResource(R.color.red_50).copy(0.5f))

                                // Reacciones y me gustas actualizados en tiempo real
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isLiked = currentUser?.id != null && currentPost.likes.contains(currentUser.id)
                                    IconButton(
                                        onClick = {
                                            currentUser?.id?.let { uid ->
                                                viewModel.toggleLike(uid)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Me gusta",
                                            tint = if (isLiked) colorResource(R.color.red_400) else Color.Gray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Text(
                                        text = "${currentPost.likesCount} reacciones",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                // Sección de listado de Comentarios
                item {
                    Text(
                        text = "Comentarios (${comments.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.deep_beige),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (comments.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aún no hay comentarios. ¡Sé el primero en comentar!",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(comments) { comment ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            colorResource(R.color.red_50),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!comment.userAvatar.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = comment.userAvatar,
                                            contentDescription = "Avatar de Comentario",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = comment.userName.take(1).uppercase(),
                                            color = colorResource(R.color.red_400),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = comment.userName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = formatTimestamp(comment.timestamp),
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = comment.text,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
