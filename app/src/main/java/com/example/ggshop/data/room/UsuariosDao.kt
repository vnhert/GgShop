
package com.example.ggshop.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // <-Importamos Flow

@Dao
interface UsuariosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: UsuarioEntity)

    @Query("SELECT * FROM Usuarios WHERE email = :email AND password = :pass LIMIT 1")
    suspend fun login(email: String, pass: String): UsuarioEntity?

    @Query("SELECT * FROM Usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): UsuarioEntity?

    @Query("UPDATE Usuarios SET nombre = :nuevoNombre, password = :nuevaPass WHERE email = :email")
    suspend fun actualizarUsuario(email: String, nuevoNombre: String, nuevaPass: String)

    @Query("UPDATE Usuarios SET imageUri = :uri WHERE email = :email")
    suspend fun actualizarFoto(email: String, uri: String)

    // Al devolver Flow, no usamos 'suspend'. Room avisara cambios.
    @Query("SELECT * FROM Usuarios")
    fun obtenerTodosEnTiempoReal(): Flow<List<UsuarioEntity>>
}