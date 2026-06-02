package com.naranjapina.heat_tourism.features.company.presentation.ManageCompany

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.company.data.model.Company
import com.naranjapina.heat_tourism.features.company.domain.usecase.GetCompanyIdByAdmin
import com.naranjapina.heat_tourism.features.company.domain.usecase.LoadCompanyDataUseCase
import com.naranjapina.heat_tourism.features.company.domain.usecase.UpdateCompanyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageCompanyViewModel(
    // private val updateCompanyUsecase: UpdateCompanyUsecase = UpdateCompanyUsecase()
    private val loadCompanyDataUseCase: LoadCompanyDataUseCase = LoadCompanyDataUseCase(),
    private val getCompanyIdByAdmin: GetCompanyIdByAdmin = GetCompanyIdByAdmin(),
    private val updateCompanyUseCase: UpdateCompanyUseCase = UpdateCompanyUseCase()
 ) : ViewModel() {

    private val _state = MutableStateFlow(ManageCompanyState())
    val state = _state.asStateFlow()

    // --- Cargar datos iniciales (Por ejemplo, al abrir la pantalla) ---
    fun loadCompanyData(name: String, logo: String?, bio: String, email: String, phone: String) {
        _state.update {
            it.copy(
                name = name,
                companyAvatarURL = logo,
                biography = bio,
                contactEmail = email,
                contactPhone = phone
            )
        }
    }

    // --- Captura de Eventos/Cambios en los inputs ---
    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onLogoPathChange(path: String?) {
        _state.update { it.copy(companyAvatarURL = path) }
    }

    fun onBiographyChange(biography: String) {
        _state.update { it.copy(biography = biography) }
    }

    fun onContactEmailChange(email: String) {
        _state.update { it.copy(contactEmail = email) }
    }

    fun onContactPhoneChange(phone: String) {
        _state.update { it.copy(contactPhone = phone) }
    }

    // Resetear la bandera de guardado exitoso tras ser consumida por la UI
    fun resetSaveStatus() {
        _state.update { it.copy(isSavedSuccess = false) }
    }

    fun loadCompanyData(userId: String?) {
        val currentState = _state.value

        viewModelScope.launch {
            var companyId = currentState.id
            Log.d("test", userId ?: "vacio")

            if(companyId == null) {
                companyId = getCompanyIdByAdmin(userId ?: "")
                _state.update { it.copy(id = companyId) }
            }

            if(companyId != null) {
                val company = loadCompanyDataUseCase(companyId!!);
                _state.update { it.copy(
                    name = company.name,
                    companyAvatarURL = company.companyAvatarURL,
                    biography = company.biography.orEmpty(),
                    contactEmail = company.contactEmail,
                    contactPhone = company.contactPhone,
                    rating = company.rating,
                    activeRoutesIds = company.activeRoutesIds,
                    activeAdministratorIds = company.activeAdministratorIds,
                    isLoading = false,
                    isSavedSuccess = true
                ) }
            }
        }
    }

    // --- Evento de Guardado ---
    fun onSaveCompanyEvent() {
        val currentState = _state.value

        viewModelScope.launch {
            // Activamos el estado de carga y limpiamos errores previos
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Invocamos el caso de uso con todos los datos del estado actual
                val updatedCompany = updateCompanyUseCase(
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

            } catch (e: IllegalArgumentException) {
                // Captura específicamente las validaciones de campos vacíos o email inválido del Usecase
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            } catch (e: Exception) {
                // Captura cualquier otro error (Falta de internet, error del servidor, etc.)
                _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Error inesperado al guardar") }
            }
        }
    }
}