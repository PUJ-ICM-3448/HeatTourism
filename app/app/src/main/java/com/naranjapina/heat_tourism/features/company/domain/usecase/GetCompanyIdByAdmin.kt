package com.naranjapina.heat_tourism.features.company.domain.usecase

import com.naranjapina.heat_tourism.features.company.data.model.Company
import com.naranjapina.heat_tourism.features.company.data.repository.CompanyRepo


class GetCompanyIdByAdmin(
    private val repo: CompanyRepo = CompanyRepo()
) {
    suspend operator fun invoke(
        userId: String
    ): String? {
        val companies = repo.getCompaniesByAdministratorId(userId);
        if (companies.isEmpty()) {
            return null
        }
        return companies[0].id
    }
}