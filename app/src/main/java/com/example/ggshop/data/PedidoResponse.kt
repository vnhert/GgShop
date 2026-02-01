package com.example.ggshop.data

import com.google.gson.annotations.SerializedName

data class PedidoResponse(
    // CAMBIO IMPORTANTE: Ahora es String para aceptar los UUID del servidor
    val id: String = "",

    val total: Double = 0.0,

    val clienteId: String = "",

    // Este campo es opcional, lo usamos para mostrar resumen en el historial
    val itemsResumen: String = "",

    val estado: String = "PENDIENTE"
)