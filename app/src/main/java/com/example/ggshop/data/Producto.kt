package com.example.ggshop.data

import com.google.gson.annotations.SerializedName

data class Producto(
    val id: Long = 0, // Valor por defecto para nuevos productos
    val nombre: String,
    val descripcion: String,

    // CORRECCIÓN 1: Cambiado a Double para evitar errores con decimales
    val precio: Double,

    val stock: Int,

    @SerializedName("imagenUrl")
    val imagenUrl: String,

    val categoria: String,

    // CORRECCIÓN 2: Agregado este campo que faltaba para el Administrador
    val imageUri: String? = null
)