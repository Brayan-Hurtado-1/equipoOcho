package com.example.equipoocho.data.repo


import android.content.Context
import com.example.equipoocho.data.local.AppDatabase
import com.example.equipoocho.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow


class InventoryRepository(context: Context) {
    private val dao = AppDatabase.get(context).productDao()


    fun products(): Flow<List<ProductEntity>> = dao.getAll()
    suspend fun product(id: Int) = dao.getById(id)
    suspend fun add(p: ProductEntity) = dao.insert(p)
    suspend fun update(p: ProductEntity) = dao.update(p)
    suspend fun remove(p: ProductEntity) = dao.delete(p)
    suspend fun total(): Double = dao.totalInventory() ?: 0.0
}