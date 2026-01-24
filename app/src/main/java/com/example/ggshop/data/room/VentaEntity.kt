package com.example.ggshop.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioEmail: String,
    val itemsResumen: String, //  productos como texto: "Mouse x1, Teclado x2"
    val total: Int,
    val fecha: Long // Para ordenar por la más reciente.
)