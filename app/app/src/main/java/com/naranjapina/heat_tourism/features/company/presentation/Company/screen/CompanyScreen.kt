package com.naranjapina.heat_tourism.features.company.presentation.Company.screen

import android.Manifest
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.data.auth.model.UserRole
import com.naranjapina.heat_tourism.features.company.domain.usecase.GetCompanyIdByAdmin
import com.naranjapina.heat_tourism.features.company.domain.usecase.LoadCompanyDataUseCase
import com.naranjapina.heat_tourism.features.company.domain.usecase.UpdateCompanyUseCase
import com.naranjapina.heat_tourism.features.company.presentation.Company.screen.view.ViewCompanyScreen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import java.io.FileOutputStream

// ... código existente ...
@Composable
fun CompanyScreen(navController: NavHostController, authViewModel: AuthViewModel, companyId: String?) {
    val authState by authViewModel.state.collectAsState()
    val user = authState.user

    when {
        // Modo Administrador
        user != null && user.roles.contains(UserRole.ADMINISTRATOR) && (companyId == null || companyId == "admin") -> {
            AdministratorMode(navController, authViewModel, accentColor = Color.Red)
        }
        // Modo Coordinador
        user != null && (user.roles.contains(UserRole.COORDINATOR) || user.roles.contains(UserRole.ADMINISTRATOR)) && companyId != null -> {
            ViewCompanyScreen(navController, companyId, accentColor = if(user.roles.contains(UserRole.ADMINISTRATOR)) Color.Red else Color(0xFF2E7D32))
        }
        // Modo General (Turista o vista pública)
        companyId != null -> {
            ViewCompanyScreen(navController, companyId)
        }
        else -> {
            navController.navigate(Screen.Home.name)
        }
    }
}
// ... código existente ...

@Composable
fun AdministratorMode(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    accentColor: Color,
    viewModel: ManageCompanyViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCompanyData(authState.user?.id)
    }

    LaunchedEffect(state.isSavedSuccess, state.error) {
        if (state.isSavedSuccess) {
            Toast.makeText(context, "Información actualizada", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveStatus()
        }
        state.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    fun saveLogo(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "company_logo_final.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
            file.absolutePath
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val file = File(context.filesDir, "temp_logo.jpg")
            val finalFile = File(context.filesDir, "company_logo_final.jpg")
            if (file.exists()) {
                file.renameTo(finalFile)
                viewModel.onLogoPathChange(finalFile.absolutePath)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.onLogoPathChange(saveLogo(it)) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (state.isLoading) item { CircularProgressIndicator(color = accentColor) }
            item {
                Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Gray), contentAlignment = Alignment.Center) {
                    AsyncImage(model = state.companyAvatarURL ?: "", contentDescription = "Logo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(enabled = !state.isLoading, onClick = {
                        val check = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (check == PackageManager.PERMISSION_GRANTED) {
                            val file = File(context.filesDir, "temp_logo.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            tempUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Cámara") }
                    Button(enabled = !state.isLoading, onClick = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Galería") }
                }
            }
            item { OutlinedTextField(value = state.name, onValueChange = { viewModel.onNameChange(it) }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !state.isLoading) }
            item { OutlinedTextField(value = state.contactEmail, onValueChange = { viewModel.onContactEmailChange(it) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, enabled = !state.isLoading) }
            item { OutlinedTextField(value = state.contactPhone, onValueChange = { viewModel.onContactPhoneChange(it) }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true, enabled = !state.isLoading) }
            item { OutlinedTextField(value = state.biography, onValueChange = { viewModel.onBiographyChange(it) }, label = { Text("Biografía") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5, enabled = !state.isLoading) }
            item { GradientButton(text = if (state.isLoading) "Guardando..." else "Guardar Cambios", color = accentColor) { if (!state.isLoading) viewModel.onSaveCompanyEvent() } }
            item {
                GradientButton(text = "Crear Ruta", color = accentColor) {
                    navController.navigate("create_route")
                }
            }
        }
    }
}

data class ManageCompanyState(
    val isLoading: Boolean = true,
    val isSavedSuccess: Boolean = false,
    val error: String? = null,
    val id: String? = null,
    val name: String = "",
    val companyAvatarURL: String? = null,
    val biography: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val rating: Double = 0.0,
    val activeRoutesIds: List<String> = emptyList(),
    val activeAdministratorIds: List<String> = emptyList()
)

class ManageCompanyViewModel(
    private val loadCompanyDataUseCase: LoadCompanyDataUseCase = LoadCompanyDataUseCase(),
    private val getCompanyIdByAdmin: GetCompanyIdByAdmin = GetCompanyIdByAdmin(),
    private val updateCompanyUseCase: UpdateCompanyUseCase = UpdateCompanyUseCase()
) : ViewModel() {
    private val _state = MutableStateFlow(ManageCompanyState())
    val state = _state.asStateFlow()

    fun onNameChange(name: String) { _state.update { it.copy(name = name) } }
    fun onLogoPathChange(path: String?) { _state.update { it.copy(companyAvatarURL = path) } }
    fun onBiographyChange(biography: String) { _state.update { it.copy(biography = biography) } }
    fun onContactEmailChange(email: String) { _state.update { it.copy(contactEmail = email) } }
    fun onContactPhoneChange(phone: String) { _state.update { it.copy(contactPhone = phone) } }
    fun resetSaveStatus() { _state.update { it.copy(isSavedSuccess = false) } }

    fun loadCompanyData(userId: String?) {
        viewModelScope.launch {
            try {
                var companyId = _state.value.id
                if(companyId == null) {
                    companyId = getCompanyIdByAdmin(userId ?: "")
                    _state.update { it.copy(id = companyId) }
                }
                if(companyId != null) {
                    val company = loadCompanyDataUseCase(companyId)
                    _state.update { it.copy(
                        name = company.name,
                        companyAvatarURL = company.companyAvatarURL,
                        biography = company.biography.orEmpty(),
                        contactEmail = company.contactEmail,
                        contactPhone = company.contactPhone,
                        rating = company.rating,
                        activeRoutesIds = company.activeRoutesIds,
                        activeAdministratorIds = company.activeAdministratorIds,
                        isLoading = false
                    ) }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun onSaveCompanyEvent() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                updateCompanyUseCase(
                    id = currentState.id,
                    name = currentState.name,
                    companyAvatarURL = currentState.companyAvatarURL,
                    biography = currentState.biography,
                    contactEmail = currentState.contactEmail,
                    contactPhone = currentState.contactPhone,
                    rating = currentState.rating,
                    activeRoutesIds = currentState.activeRoutesIds,
                    activeAdministratorIds = currentState.activeAdministratorIds
                )
                _state.update { it.copy(isLoading = false, isSavedSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Error al guardar") }
            }
        }
    }
}
