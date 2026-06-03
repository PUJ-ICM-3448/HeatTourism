package com.naranjapina.heat_tourism.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryApi {
    @GET("name/{name}")
    suspend fun getCountryInfo(@Path("name") name: String): List<CountryResponse>
}

data class CountryResponse(
    val name: CountryName,
    val flags: CountryFlags,
    val population: Long,
    val region: String
)

data class CountryName(val common: String)
data class CountryFlags(val png: String)

class CountryRepository {
    private val api: CountryApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(CountryApi::class.java)
    }

    suspend fun getCountry(name: String): CountryResponse? {
        return try {
            api.getCountryInfo(name).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
