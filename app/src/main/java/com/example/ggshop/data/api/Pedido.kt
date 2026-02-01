package com.example.ggshop.data

data class PedidoRequest(
    val clienteId: String,
    val estado: String = "PENDIENTE",
    val total: Double,               // CAMBIADO A DOUBLE
    val items: List<ItemPedidoRequest>
)

data class ItemPedidoRequest(
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double,      // CAMBIADO A DOUBLE
    val subtotal: Double             // CAMBIADO A DOUBLE
)