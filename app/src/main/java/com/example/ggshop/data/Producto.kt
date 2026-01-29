package com.example.ggshop.data

data class Producto(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val stock: Int,
    val imagenUrl: String,      // imágenes predeterminadas (R.drawable...)
    val categoria: String,
    val imageUri: String? = null // imágenes subidas por el admin
)