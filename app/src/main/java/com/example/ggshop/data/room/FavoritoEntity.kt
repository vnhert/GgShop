package com.example.ggshop.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioEmail: String, // Para saber de quién es el favorito
    val productoId: Long      // El ID del producto que le gustó
)