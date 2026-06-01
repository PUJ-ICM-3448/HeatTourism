package com.naranjapina.heat_tourism.features.social.presentation.Profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.DestinationCardData
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.component.HorizontalDestinationCard
import com.naranjapina.heat_tourism.core.component.StatRowItemData
import com.naranjapina.heat_tourism.core.component.StatsRow
import com.naranjapina.heat_tourism.core.component.TitleAndButton
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }

    var imagePath by remember { mutableStateOf(prefs.getString("profile_image", null)) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "profile_final.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            val path = file.absolutePath
            prefs.edit { putString("profile_image", path) }
            path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val file = File(context.filesDir, "perfil_temp.jpg")
            val finalFile = File(context.filesDir, "profile_final.jpg")
            if (file.exists()) {
                file.renameTo(finalFile)
                val path = finalFile.absolutePath
                imagePath = path
                prefs.edit { putString("profile_image", path) }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            imagePath = saveImageToInternalStorage(it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.filesDir, "perfil_temp.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val authState by authViewModel.state.collectAsState()
    LaunchedEffect(
        authState.user
    ) {
        if (authState.user == null)
            navController.navigate(Screen.LogIn.name)
    }

    MenuBottonLayout(activeName = "profile", navController = navController) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                Column(
                    modifier = Modifier
                        .background(colorResource(R.color.red_50))
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Mi Perfil", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(.8f),
                                    shape = RoundedCornerShape(100)
                                )
                                .padding(10.dp)
                                .clickable(
                                    onClick = {
                                        authViewModel.logOutUser()
                                    }
                                )
                        )
                    }

                    Row(
                        modifier = Modifier.height(130.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = imagePath
                                    ?: "https://avatars.githubusercontent.com/u/62490806?v=4",
                                contentDescription = "Avatar perfil",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(3.dp, Color.White, RoundedCornerShape(100))
                                    .clip(RoundedCornerShape(100)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Miguel Vargas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            GradientButton(text = "Tomar foto") {
                                val permissionCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    val file = File(context.filesDir, "perfil_temp.jpg")
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.provider",
                                        file
                                    )
                                    tempUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }

                            GradientButton(text = "Galería") {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        }
                    }

                    StatsRow(
                        items = listOf(
                            StatRowItemData(title = "Viajes", value = 12),
                            StatRowItemData(title = "Lugares", value = 47),
                            StatRowItemData(title = "Seguidores", value = 234)
                        )
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(15.dp, 0.dp)) {
                    Spacer(modifier = Modifier.height(15.dp))
                    TitleAndButton("Mis viajes", "Ver todos")
                    repeat(2) {
                        HorizontalDestinationCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(vertical = 8.dp),
                            data = DestinationCardData(
                                destinationName = "Park Güell, Barcelona",
                                destinationScore = 4.783f,
                                imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
                                contentDescription = ""
                            ),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
