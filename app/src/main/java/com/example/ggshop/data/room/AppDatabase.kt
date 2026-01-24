package com.example.ggshop.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// SUBIMOS VERSIÓN A 3 por VentaEntity
@Database(
    entities = [UsuarioEntity::class, FavoritoEntity::class, VentaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuariosDao(): UsuariosDao
    abstract fun favoritosDao(): FavoritosDao
    abstract fun ventasDao(): VentasDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ggshop_database"
                )
                    .fallbackToDestructiveMigration() // Borra y crea de nuevo si cambia versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}