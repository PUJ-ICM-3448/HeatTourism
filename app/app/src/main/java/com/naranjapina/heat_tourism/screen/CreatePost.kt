package com.naranjapina.heat_tourism.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.DestinationCardData
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.component.HorizontalDestinationCard
import com.naranjapina.heat_tourism.component.LocationPicker
import com.naranjapina.heat_tourism.component.TitleAndButton
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.utils.beigeGradientBrush
import com.naranjapina.heat_tourism.utils.bottomBorder
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreatePostScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("post_prefs", Context.MODE_PRIVATE) }

    var description by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf(prefs.getString("last_post_image", null)) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    fun saveImage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "post_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val file = File(context.filesDir, "temp_post.jpg")
            val finalFile = File(context.filesDir, "post_${System.currentTimeMillis()}.jpg")
            if (file.exists()) {
                file.renameTo(finalFile)
                imagePath = finalFile.absolutePath
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imagePath = saveImage(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.filesDir, "temp_post.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val takePhoto = {
        val check = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (check == PackageManager.PERMISSION_GRANTED) {
            val file = File(context.filesDir, "temp_post.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val pickGallery = {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Scaffold(
        containerColor = colorResource(R.color.beige)
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(
                0.dp, 0.dp, 0.dp, paddingValues.calculateBottomPadding()
            )
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .bottomBorder(
                            strokeWidth = 1.dp,
                            color = colorResource(R.color.red_100)
                        )
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowLeft,
                        contentDescription = "Volver",
                        modifier = Modifier
                            .height(35.dp)
                            .width(35.dp)
                            .clickable(
                                onClick = {
                                    if (navController.previousBackStackEntry == null)
                                        navController.navigate(Screen.Home.name)
                                    else navController.navigateUp()
                                }
                            )
                    )
                    Text(
                        text = "Nueva Publicación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    GradientButton(
                        text = "Publicar"
                    ) {
                        if (imagePath != null) {
                            prefs.edit { putString("last_post_image", imagePath) }
                            Toast.makeText(context, "Post Guardado", Toast.LENGTH_SHORT).show()
                            if (navController.previousBackStackEntry == null)
                                navController.navigate(Screen.Home.name)
                            else navController.navigateUp()
                        } else {
                            Toast.makeText(context, "Falta la imagen", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Photo Area
            item {
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp)
                ) {
                    Text(
                        text = "Foto del lugar",
                        color = colorResource(R.color.deep_beige),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(
                                brush = beigeGradientBrush(),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .border(
                                shape = RoundedCornerShape(15.dp),
                                color = colorResource(R.color.red_50),
                                width = 1.dp
                            )
                            .clip(RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagePath != null) {
                            AsyncImage(
                                model = imagePath,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { pickGallery() }, // Permitir cambiarla tocando de nuevo
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.camera),
                                    contentDescription = "Camera",
                                    colorFilter = ColorFilter.tint(
                                        colorResource(R.color.red_400)
                                    ),
                                    modifier = Modifier
                                        .background(
                                            color = colorResource(R.color.red_400).copy(.25f),
                                            shape = RoundedCornerShape(percent = 100)
                                        )
                                        .padding(20.dp)
                                        .clickable { takePhoto() }
                                )

                            }

                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GradientButton(
                            text = "Tomar Foto",
                            modifier = Modifier.weight(1f)
                        ) {
                            takePhoto()
                        }

                        Spacer(Modifier.width(15.dp))

                        GradientButton(
                            text = "Seleccionar Foto",
                            modifier = Modifier.weight(1f)
                        ) {
                            pickGallery()
                        }
                    }
                }
            }

            item {
                LocationPicker(
                    modifier = Modifier.padding(15.dp, 0.dp)
                )
            }

            // Description Area
            item {
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Descripción",
                        color = colorResource(R.color.deep_beige),
                        fontSize = 18.sp
                    )
                    TextField(
                        singleLine = false,
                        minLines = 5,
                        value = description,
                        onValueChange = {
                            if (it.length <= 500) {
                                description = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(15.dp),
                                color = colorResource(R.color.red_50)
                            ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text("Comparte tu experiencia")
                        }
                    )
                    Text(
                        text = "${description.length}/500 caracteres",
                        color = colorResource(R.color.deep_beige).copy(.7f),
                        fontSize = 14.sp
                    )
                }
            }

            // Route Area
            item {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    TitleAndButton(
                        "Ruta",
                        "Cambiar"
                    )
                    HorizontalDestinationCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 0.dp, vertical = 8.dp),
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