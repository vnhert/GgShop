package com.example.ggshop.repository

import com.example.ggshop.data.Producto
import com.example.ggshop.data.api.ApiService
import com.example.ggshop.data.api.RetrofitInstance

class ProductoRepository(
    private val api: ApiService = RetrofitInstance.api // Mismo estilo: usa la instancia por defecto
) {

    // Obtiene los productos tecnológicos (Celulares, Laptops, etc.)
    suspend fun getProductos(): List<Producto> {
        return api.getProductos()
    }
}