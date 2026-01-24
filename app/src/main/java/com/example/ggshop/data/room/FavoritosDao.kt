package com.example.ggshop.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritosDao {
    @Insert
    suspend fun agregarFavorito(favorito: FavoritoEntity)

    // Borra un favorito específico de un usuario
    @Query("DELETE FROM favoritos WHERE usuarioEmail = :email AND productoId = :idProducto")
    suspend fun eliminarFavorito(email: String, idProducto: Long)

    // Obtiene solo los favoritos de ESTE usuario en tiempo real
    @Query("SELECT * FROM favoritos WHERE usuarioEmail = :email")
    fun obtenerFavoritosPorUsuario(email: String): Flow<List<FavoritoEntity>>

    // Verifica si un producto ya es favorito (útil para el ícono)
    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE usuarioEmail = :email AND productoId = :idProducto)")
    suspend fun esFavorito(email: String, idProducto: Long): Boolean
}