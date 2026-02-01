package com.example.ggshop.data

import com.google.gson.annotations.SerializedName

data class Usuario(
    // CAMBIO IMPORTANTE:
    // 1. Usamos 'alternate' para buscar "id", "_id" o "userId". ¡Cazaremos el ID como sea!
    // 2. Lo cambiamos a String para que acepte letras y números.
    @SerializedName(value = "id", alternate = ["_id", "userId"])
    val id: String = "",

    @SerializedName("nombreCompleto")
    val nombre: String,

    @SerializedName("username")
    val email: String,

    val password: String = "",
    val rol: String = "user",
    val direccion: String = ""
)