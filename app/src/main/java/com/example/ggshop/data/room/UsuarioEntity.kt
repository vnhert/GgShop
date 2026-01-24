package com.example.ggshop.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val email: String, // Usamos el email como ID único
    val nombre: String,
    val password: String,
    val imageUri: String? = null, // Para guardar la foto de perfil
    val puntos: Int = 0
)