package com.naranjapina.heat_tourism.features.company.data.repository

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.company.data.model.Company
import com.naranjapina.heat_tourism.features.company.data.model.CompanyException
import kotlinx.coroutines.tasks.await

class CompanyRepo {
    private val db: FirebaseFirestore = Firebase.firestore
    private val collectionRef = db.collection("companies")

    /**
     * Obtiene una empresa por su ID de documento en Firestore.
     */
    suspend fun getCompanyById(id: String): Company {
        try {
            val doc = collectionRef.document(id).get().await()
            if (doc.exists()) {
                return mapToCompany(doc)
            } else {
                throw CompanyException.CompanyNotFoundException()
            }
        } catch (e: FirebaseNetworkException) {
            throw CompanyException.NetworkException()
        } catch (e: CompanyException) {
            throw e
        } catch (e: Exception) {
            throw CompanyException.UnknownCompanyException(e.localizedMessage)
        }
    }

    /**
     * Obtiene todas las empresas asociadas a un ID de administrador.
     */
    suspend fun getCompaniesByAdministratorId(adminId: String): List<Company> {
        try {
            val querySnapshot = collectionRef
                .whereArrayContains("activeAdministratorIds", adminId)
                .get()
                .await()
            return querySnapshot.documents.map { mapToCompany(it) }
        } catch (e: FirebaseNetworkException) {
            throw CompanyException.NetworkException()
        } catch (e: Exception) {
            throw CompanyException.UnknownCompanyException(e.localizedMessage)
        }
    }

    /**
     * Obtiene todas las empresas registradas en Firestore.
     */
    suspend fun getAllCompanies(): List<Company> {
        try {
            val querySnapshot = collectionRef.get().await()
            return querySnapshot.documents.map { mapToCompany(it) }
        } catch (e: FirebaseNetworkException) {
            throw CompanyException.NetworkException()
        } catch (e: Exception) {
            throw CompanyException.UnknownCompanyException(e.localizedMessage)
        }
    }

    /**
     * Registra/crea una nueva empresa en Firestore.
     * Retorna el objeto Company conteniendo el ID auto-generado.
     */
    suspend fun createCompany(company: Company): Company {
        try {
            val payload = companyToMap(company)
            val docRef = collectionRef.add(payload).await()
            return company.copy(id = docRef.id)
        } catch (e: FirebaseNetworkException) {
            throw CompanyException.NetworkException()
        } catch (e: Exception) {
            throw CompanyException.UnknownCompanyException(e.localizedMessage)
        }
    }

    /**
     * Actualiza la información de una empresa existente en Firestore.
     */
    suspend fun updateCompany(company: Company) {
        val id = company.id
            ?: throw CompanyException.UnknownCompanyException("El ID de la empresa no puede ser nulo para actualizar")
        try {
            val payload = companyToMap(company)
            collectionRef.document(id).set(payload).await()
        } catch (e: FirebaseNetworkException) {
            throw CompanyException.NetworkException()
        } catch (e: Exception) {
            throw CompanyException.UnknownCompanyException(e.localizedMessage)
        }
    }

    /**
     * Elimina una empresa de Firestore por su ID.
     */
    suspend fun deleteCompany(id: String) {
        try {
            collectionRef.document(id).delete().await()
        } catch (e: FirebaseNetworkException) {
            throw CompanyException.NetworkException()
        } catch (e: Exception) {
            throw CompanyException.UnknownCompanyException(e.localizedMessage)
        }
    }

    /**
     * Convierte un objeto Company a un Map compatible con Firestore.
     */
    private fun companyToMap(company: Company): Map<String, Any?> {
        return hashMapOf(
            "name" to company.name,
            "companyAvatarURL" to company.companyAvatarURL,
            "biography" to company.biography,
            "contactEmail" to company.contactEmail,
            "contactPhone" to company.contactPhone,
            "activeRoutesIds" to company.activeRoutesIds,
            "activeAdministratorIds" to company.activeAdministratorIds,
            "rating" to company.rating
        )
    }

    /**
     * Mapea un DocumentSnapshot de Firestore a un objeto Company de forma segura.
     */
    private fun mapToCompany(document: com.google.firebase.firestore.DocumentSnapshot): Company {
        @Suppress("UNCHECKED_CAST")
        val activeRoutesIds = document.get("activeRoutesIds") as? List<String> ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        val activeAdministratorIds = document.get("activeAdministratorIds") as? List<String> ?: emptyList()

        return Company(
            id = document.id,
            name = document.getString("name").orEmpty(),
            companyAvatarURL = document.getString("companyAvatarURL"),
            biography = document.getString("biography"),
            contactEmail = document.getString("contactEmail").orEmpty(),
            contactPhone = document.getString("contactPhone").orEmpty(),
            activeRoutesIds = activeRoutesIds,
            activeAdministratorIds = activeAdministratorIds,
            rating = document.getDouble("rating") ?: 0.0
        )
    }
}
