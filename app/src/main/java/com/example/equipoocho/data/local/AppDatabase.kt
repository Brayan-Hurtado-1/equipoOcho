// Paquete donde se encuentra la clase AppDatabase
package com.example.equipoocho.data.local

// Importación de las clases necesarias para trabajar con Room Database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Anotación para declarar que esta clase es una base de datos de Room
@Database(entities = [ProductEntity::class], version = 1)  // 'ProductEntity' es la entidad que será parte de la base de datos, y la versión se establece en 1
abstract class AppDatabase : RoomDatabase() {  // Clase abstracta que extiende RoomDatabase

    // Función abstracta que devuelve el DAO (Data Access Object) correspondiente para 'ProductEntity'
    abstract fun productDao(): ProductDao

    // Compañero (companion object) para acceder de manera singleton a la instancia de la base de datos
    companion object {
        // Instancia privada y volátil de la base de datos
        @Volatile private var INSTANCE: AppDatabase? = null
        
        // Función para obtener la instancia de la base de datos, la crea si no existe
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {  
            // Bloque synchronized para garantizar que solo un hilo puede crear la instancia a la vez
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "inventory.db")  // Se crea la base de datos con el nombre 'inventory.db'
                .fallbackToDestructiveMigration()  // Si hay un error en la migración, se destruye la base de datos y se vuelve a crear
                .build()  // Construye la base de datos
                .also { INSTANCE = it }  // Se asigna la instancia creada a la variable INSTANCE
        }
    }
}
