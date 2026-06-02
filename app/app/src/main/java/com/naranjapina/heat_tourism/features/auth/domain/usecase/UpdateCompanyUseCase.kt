package com.naranjapina.heat_tourism.features.company.domain.usecase

import com.naranjapina.heat_tourism.features.company.data.model.Company
import com.naranjapina.heat_tourism.features.company.data.repository.CompanyRepo

class UpdateCompanyUseCase(
     private val repo: CompanyRepo = CompanyRepo()
) {
    suspend operator fun invoke(
        id: String? = null,
        name: String,
        companyAvatarURL: String?,
        biography: String,
        contactEmail: String,
        contactPhone: String,
        rating: Double = 0.0,
        activeRoutesIds: List<String> = emptyList(),
        activeAdministratorIds: List<String> = emptyList()
    ): Company? {

        if (name.isBlank()) {
            throw IllegalArgumentException("El nombre de la empresa no puede estar vacío.")
        }
        if (contactEmail.isBlank()) {
            throw IllegalArgumentException("El correo de contacto no puede estar vacío.")
        }
        if (!contactEmail.contains("@")) {
            throw IllegalArgumentException("El correo electrónico no es válido.")
        }
        if (contactPhone.isBlank()) {
            throw IllegalArgumentException("El teléfono de contacto no puede estar vacío.")
        }

        val updatedCompany = Company(
            id = id,
            name = name,
            companyAvatarURL = companyAvatarURL,
            biography = biography,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            rating = rating,
            activeRoutesIds = activeRoutesIds,
            activeAdministratorIds = activeAdministratorIds
        )

         return repo.updateCompany(updatedCompany)
    }
}