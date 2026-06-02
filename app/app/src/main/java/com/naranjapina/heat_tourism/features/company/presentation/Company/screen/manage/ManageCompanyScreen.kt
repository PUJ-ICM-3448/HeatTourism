package com.naranjapina.heat_tourism.features.company.presentation.Company.screen.manage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.features.company.presentation.ManageCompany.ManageCompanyViewModel
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ManageCompanyScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    viewModel: ManageCompanyViewModel = viewModel() // Inyección por defecto de Jetpack Compose Lifecycle
) {
    val context = LocalContext.current

    // Recolectar el estado del ViewModel de manera segura para Compose
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()

    var tempUri by remember { mutableStateOf<Uri?>(null) }


    LaunchedEffect(Unit) {
        viewModel.loadCompanyData(authState.user?.id)
    }

    // --- ESCUCHAR ALERTAS DE ÉXITO O ERROR ---
    LaunchedEffect(state.isSavedSuccess, state.error) {
        if (state.isSavedSuccess) {
            Toast.makeText(context, "Información actualizada", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveStatus()
        }
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // --- LÓGICA DE GESTIÓN DE LOGO ---
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
        if (success) {
            val file = File(context.filesDir, "temp_logo.jpg")
            val finalFile = File(context.filesDir, "company_logo_final.jpg")
            if (file.exists()) {
                file.renameTo(finalFile)
                viewModel.onLogoPathChange(finalFile.absolutePath)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val savedPath = saveLogo(it)
            viewModel.onLogoPathChange(savedPath)
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

    // --- DISEÑO DE LA INTERFAZ ---
    MenuBottonLayout(activeName = "profile", navController = navController) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Indicador visual de carga superior (opcional pero muy útil)
            if (state.isLoading) {
                item {
                    CircularProgressIndicator(color = Color.Red)
                }
            }

            // Imagen / Avatar
            item {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = state.companyAvatarURL ?: "",
                        contentDescription = "Logo de empresa",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Botones multimedia (Deshabilitados en Loading)
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        enabled = !state.isLoading,
                        onClick = {
                            val check = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (check == PackageManager.PERMISSION_GRANTED) {
                                val file = File(context.filesDir, "temp_logo.jpg")
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                tempUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    ) {
                        Text("Cámara")
                    }

                    Button(
                        enabled = !state.isLoading,
                        onClick = {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    ) {
                        Text("Galería")
                    }
                }
            }

            // Input Nombre de la Empresa
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Nombre de la Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.isLoading
                )
            }

            // Input Correo
            item {
                OutlinedTextField(
                    value = state.contactEmail,
                    onValueChange = { viewModel.onContactEmailChange(it) },
                    label = { Text("Correo Electrónico de Contacto") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = !state.isLoading
                )
            }

            // Input Teléfono
            item {
                OutlinedTextField(
                    value = state.contactPhone,
                    onValueChange = { viewModel.onContactPhoneChange(it) },
                    label = { Text("Teléfono de Contacto") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    enabled = !state.isLoading
                )
            }

            // Input Biografía
            item {
                OutlinedTextField(
                    value = state.biography,
                    onValueChange = { viewModel.onBiographyChange(it) },
                    label = { Text("Biografía de la Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !state.isLoading
                )
            }

            // Botón de Guardado
            item {
                // Nota: Asegúrate de que tu GradientButton maneje internamente un parámetro 'enabled'
                // o pásale una lambda vacía para simular bloqueo si está cargando.
                GradientButton(
                    text = if (state.isLoading) "Guardando..." else "Guardar Cambios"
                ) {
                    if (!state.isLoading) {
                        viewModel.onSaveCompanyEvent()
                    }
                }
            }
        }
    }
}