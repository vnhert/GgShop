package com.example.ggshop.data

data class Producto(
    val id: Long?,
    val nombre: String,      // Ej: "Laptop Gamer RTX 4060"
    val descripcion: String, // Ej: "16GB RAM, 512GB SSD..."
    val precio: Int,
    val stock: Int,
    val imagenUrl: Int,
    val categoria: String    // Ej: "Laptops", "Celulares", "Audio"
)