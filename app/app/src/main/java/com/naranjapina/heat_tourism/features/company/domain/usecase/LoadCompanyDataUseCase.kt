package com.naranjapina.heat_tourism.features.company.domain.usecase

import com.naranjapina.heat_tourism.features.company.data.model.Company
import com.naranjapina.heat_tourism.features.company.data.repository.CompanyRepo


class LoadCompanyDataUseCase(
    private val repo: CompanyRepo = CompanyRepo()
) {
    suspend operator fun invoke(
        companyId: String
    ): Company {
        return repo.getCompanyById(companyId)
    }
}