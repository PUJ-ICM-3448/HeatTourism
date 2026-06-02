package com.naranjapina.heat_tourism.features.company.data.model

sealed class CompanyException : Exception() {
    class CompanyNotFoundException : CompanyException()
    class NetworkException : CompanyException()
    class UnknownCompanyException(override val message: String?) : CompanyException()
}
