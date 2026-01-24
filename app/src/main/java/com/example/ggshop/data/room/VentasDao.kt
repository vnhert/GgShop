package com.example.ggshop.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VentasDao {
    @Insert
    suspend fun registrarVenta(venta: VentaEntity)

    // El Admin verá todas las ventas, de la más nueva a la más vieja
    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun obtenerHistorialVentas(): Flow<List<VentaEntity>>
}