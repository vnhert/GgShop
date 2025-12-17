package com.example.ggshop.data.api

import com.example.ggshop.data.Producto
import retrofit2.http.GET

interface ApiService {

    @GET("productos")
    suspend fun getProductos(): List<Producto>
}