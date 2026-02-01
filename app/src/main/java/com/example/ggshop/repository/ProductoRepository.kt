package com.example.ggshop.repository

import android.util.Log
import com.example.ggshop.data.Usuario
import com.example.ggshop.data.Producto
import com.example.ggshop.data.PedidoRequest
import com.example.ggshop.data.PedidoResponse
import com.example.ggshop.data.api.LoginRequest
import com.example.ggshop.data.api.RegistroRequest
import com.example.ggshop.data.api.RetrofitInstance

class ProductoRepository {
    private val api = RetrofitInstance.api

    // --- AUTH ---
    suspend fun loginEnBackend(u: String, p: String): Usuario? {
        return try {
            api.login(LoginRequest(u, p))
        } catch (e: Exception) {
            Log.e("REPO", "Login Error: ${e.message}")
            null
        }
    }

    suspend fun registrarUsuario(nombre: String, correo: String, pass: String): Boolean {
        return try {
            val req = RegistroRequest(nombre, correo, pass)
            api.registrar(req)
            true
        } catch (e: Exception) {
            Log.e("REPO", "Error registro: ${e.message}")
            false
        }
    }

    // --- PRODUCTOS ---
    suspend fun getProductosDeBackend(): List<Producto> {
        return try { api.obtenerProductos() } catch (e: Exception) { emptyList() }
    }

    suspend fun crearProducto(nuevo: Producto): Boolean {
        return try {
            api.crearProducto(nuevo)
            true
        } catch (e: Exception) {
            Log.e("REPO", "Error al crear producto: ${e.message}")
            false
        }
    }

    // NUEVO: Función para actualizar un producto existente (Evita duplicados)
    suspend fun actualizarProducto(producto: Producto): Boolean {
        return try {
            // Se asume que en ApiService tienes un @PUT("api/products/{id}") o similar
            api.actualizarProducto(producto.id, producto)
            true
        } catch (e: Exception) {
            Log.e("REPO", "Error al actualizar producto: ${e.message}")
            false
        }
    }

    suspend fun eliminarProducto(id: Long): Boolean {
        return try {
            api.eliminarProducto(id)
            true
        } catch (e: Exception) {
            Log.e("REPO", "Error al eliminar producto: ${e.message}")
            false
        }
    }

    // --- PEDIDOS ---
    suspend fun enviarPedido(pedido: PedidoRequest): Boolean {
        return try {
            api.crearPedido(pedido)
            true
        } catch (e: Exception) {
            Log.e("REPO", "Error al enviar pedido: ${e.message}")
            false
        }
    }

    suspend fun obtenerHistorial(usuario: String): List<PedidoResponse> {
        return try { api.obtenerPedidosUsuario(usuario) } catch (e: Exception) { emptyList() }
    }

    // --- FUNCIONES PARA ADMIN ---
    suspend fun obtenerTodosLosUsuarios(): List<Usuario> {
        return try { api.obtenerUsuarios() } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerTodasLasVentas(): List<PedidoResponse> {
        return try { api.obtenerTodasLasVentas() } catch (e: Exception) { emptyList() }
    }
}