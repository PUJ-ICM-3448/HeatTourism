package com.naranjapina.heat_tourism.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}


data class WeatherResponse(
    val main: Main
)

data class Main(
    val temp: Float
)

class WeatherRepository {

    private val apiKey = "1732469d82bbed55cc73c14c2c83ca96"

    private val api: WeatherApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(WeatherApi::class.java)
    }

    suspend fun getTemperature(city: String): Float {
        return api.getWeather(city, apiKey).main.temp
    }
}