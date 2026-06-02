package com.naranjapina.heat_tourism.features.company.presentation.ManageCompany

import com.naranjapina.heat_tourism.features.company.data.model.Company

data class ManageCompanyState(
    // Estados de la interfaz/flujo
    val isLoading: Boolean = true,
    val isSavedSuccess: Boolean = false,
    val error: String? = null,

    // Campos del formulario alineados a tu modelo Company
    val id: String? = null,
    val name: String = "",
    val companyAvatarURL: String? = null, // Reemplaza localmente a companyAvatarURL
    val biography: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val rating: Double = 0.0,
    val activeRoutesIds: List<String> = emptyList(),
    val activeAdministratorIds: List<String> = emptyList()
)