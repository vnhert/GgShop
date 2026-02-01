package com.example.ggshop.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // AGREGAR :8082 AL FINAL. ¡ESTO ES LO QUE FALTA!
    private const val BASE_URL = "http://192.168.1.5:8082/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
