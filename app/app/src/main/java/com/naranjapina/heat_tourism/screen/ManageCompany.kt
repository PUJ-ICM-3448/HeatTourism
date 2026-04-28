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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import java.io.File
import java.io.FileOutputStream

@Composable
fun ManageCompanyScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("company_prefs", Context.MODE_PRIVATE) }

    var companyName by remember { mutableStateOf(prefs.getString("name", "Mi Empresa") ?: "Mi Empresa") }
    var logoPath by remember { mutableStateOf(prefs.getString("logo", null)) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    fun saveLogo(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "company_logo_final.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            // Al tomar la foto, ya está en almacenamiento interno (temp_logo.jpg)
            // La movemos o simplemente usamos esa ruta
            val file = File(context.filesDir, "temp_logo.jpg")
            val finalFile = File(context.filesDir, "company_logo_final.jpg")
            if (file.exists()) {
                file.renameTo(finalFile)
                logoPath = finalFile.absolutePath
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            logoPath = saveLogo(it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.filesDir, "temp_logo.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    MenuBottonLayout(activeName = "profile", navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = logoPath ?: "https://via.placeholder.com/150",
                    contentDescription = "Logo de empresa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = {
                    val check = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (check == PackageManager.PERMISSION_GRANTED) {
                        val file = File(context.filesDir, "temp_logo.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        tempUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text("Cámara")
                }

                Button(onClick = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Galería")
                }
            }

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Nombre de la Empresa") },
                modifier = Modifier.fillMaxWidth()
            )

            GradientButton(text = "Guardar Cambios") {
                prefs.edit().apply {
                    putString("name", companyName)
                    putString("logo", logoPath)
                    apply()
                }
                Toast.makeText(context, "Información actualizada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
