package com.example.equipoocho.data.repo

// Importaciones necesarias para trabajar con la base de datos y corrutinas
import android.content.Context
import com.example.equipoocho.data.local.AppDatabase
import com.example.equipoocho.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

// Clase Repository para gestionar el inventario de productos
class InventoryRepository(context: Context) {

    // Se obtiene el DAO de la base de datos usando la instancia de AppDatabase
    private val dao = AppDatabase.get(context).productDao()

    // Método para obtener todos los productos en un Flow (listado reactivo)
    fun products(): Flow<List<ProductEntity>> = dao.getAll()

    // Método suspendido para obtener un producto por su ID
    suspend fun product(id: Int) = dao.getById(id)

    // Método suspendido para agregar un nuevo producto a la base de datos
    suspend fun add(p: ProductEntity) = dao.insert(p)

    // Método suspendido para actualizar un producto existente en la base de datos
    suspend fun update(p: ProductEntity) = dao.update(p)

    // Método suspendido para eliminar un producto de la base de datos
    suspend fun remove(p: ProductEntity) = dao.delete(p)

    // Método suspendido para calcular el total del inventario (valor total)
    suspend fun total(): Double = dao.totalInventory() ?: 0.0  // Devuelve 0.0 si el valor es null
}
