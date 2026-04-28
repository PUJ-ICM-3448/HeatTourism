package com.naranjapina.heat_tourism.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
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
        }
    }

    MenuBottonLayout(activeName = "create", navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Crear Publicación", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (imagePath != null) {
                    AsyncImage(
                        model = imagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("No se ha seleccionado imagen")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    GradientButton(text = "Cámara") {
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
                }

                Box(modifier = Modifier.weight(1f)) {
                    GradientButton(text = "Galería") {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("¿Qué estás pensando?") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            GradientButton(text = "Publicar", modifier = Modifier.fillMaxWidth()) {
                if (imagePath != null) {
                    prefs.edit().putString("last_post_image", imagePath).apply()
                    Toast.makeText(context, "Post Guardado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Falta la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
