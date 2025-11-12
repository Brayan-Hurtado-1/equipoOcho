package com.example.equipoocho.data.local

// Importaciones necesarias de Room y Kotlin para flujos
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Interfaz DAO para la entidad ProductEntity
@Dao
interface ProductDao {

    // Consulta para obtener todos los productos ordenados por nombre en orden ascendente
    @Query("SELECT * FROM products ORDER BY name ASC")
    // Devuelve un Flow que contiene una lista de todos los productos, lo que permite la reactividad
    fun getAll(): Flow<List<ProductEntity>> 

    // Consulta para obtener un producto por su ID
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    // Suspende la ejecución hasta que se obtenga el producto o se termine la consulta
    suspend fun getById(id: Int): ProductEntity? 

    // Función para insertar un producto en la base de datos
    @Insert(onConflict = OnConflictStrategy.ABORT)
    // Si hay un conflicto (por ejemplo, un producto con el mismo ID), la operación se aborta
    suspend fun insert(product: ProductEntity) 

    // Función para actualizar un producto existente en la base de datos
    @Update
    // Suspende la ejecución hasta que se complete la actualización
    suspend fun update(product: ProductEntity) 

    // Función para eliminar un producto de la base de datos
    @Delete
    // Suspende la ejecución hasta que se complete la eliminación
    suspend fun delete(product: ProductEntity) 

    // Consulta para calcular el valor total del inventario (precio * cantidad de cada producto)
    @Query("SELECT SUM(price * quantity) FROM products")
    // Devuelve el total del inventario como un valor Double (puede ser null si no hay datos)
    suspend fun totalInventory(): Double?
}
