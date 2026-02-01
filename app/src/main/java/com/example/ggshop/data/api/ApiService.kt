package com.example.ggshop.data.api

import com.example.ggshop.data.Usuario
import com.example.ggshop.data.Producto
import com.example.ggshop.data.PedidoRequest
import com.example.ggshop.data.PedidoResponse
import retrofit2.http.*

data class LoginRequest(val username: String, val password: String)

data class RegistroRequest(
    val nombreCompleto: String,
    val username: String,
    val password: String,
    val rol: String = "CLIENTE"
)

interface ApiService {
    // --- USUARIOS ---
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Usuario?

    @POST("api/usuarios/registro")
    suspend fun registrar(@Body request: RegistroRequest): Usuario

    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>

    // --- PRODUCTOS ---
    @GET("api/products")
    suspend fun obtenerProductos(): List<Producto>

    // Crear producto nuevo
    @POST("api/products")
    suspend fun crearProducto(@Body producto: Producto): Producto

    // ACTUALIZAR PRODUCTO (Evita que se dupliquen al editar stock)
    @PUT("api/products/{id}")
    suspend fun actualizarProducto(@Path("id") id: Long, @Body producto: Producto): Producto

    // Eliminar producto por ID
    @DELETE("api/products/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Unit

    // --- PEDIDOS ---
    @POST("api/pedidos")
    suspend fun crearPedido(@Body pedido: PedidoRequest): PedidoResponse

    @GET("api/pedidos/usuario/{clienteId}")
    suspend fun obtenerPedidosUsuario(@Path("clienteId") clienteId: String): List<PedidoResponse>

    @GET("api/pedidos")
    suspend fun obtenerTodasLasVentas(): List<PedidoResponse>
}