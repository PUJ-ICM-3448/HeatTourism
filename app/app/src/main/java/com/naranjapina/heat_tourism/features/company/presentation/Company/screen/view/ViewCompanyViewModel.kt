package com.naranjapina.heat_tourism.features.company.presentation.ManageCompany

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.company.domain.usecase.GetCompanyIdByAdmin
import com.naranjapina.heat_tourism.features.company.domain.usecase.LoadCompanyDataUseCase
import com.naranjapina.heat_tourism.features.company.domain.usecase.UpdateCompanyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewCompanyViewModel(
    // private val updateCompanyUsecase: UpdateCompanyUsecase = UpdateCompanyUsecase()
    private val loadCompanyDataUseCase: LoadCompanyDataUseCase = LoadCompanyDataUseCase(),
    private val getCompanyIdByAdmin: GetCompanyIdByAdmin = GetCompanyIdByAdmin(),
    private val updateCompanyUseCase: UpdateCompanyUseCase = UpdateCompanyUseCase()
 ) : ViewModel() {

    private val _state = MutableStateFlow(ViewCompanyState())
    val state = _state.asStateFlow()


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

    fun loadCompanyData(companyId: String?) {
        viewModelScope.launch {
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
                    isSavedSuccess = true,
                    id = companyId
                ) }
            }
        }
    }

}