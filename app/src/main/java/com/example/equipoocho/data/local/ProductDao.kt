package com.example.equipoocho.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): Flow<List<ProductEntity>>


    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ProductEntity?


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: ProductEntity)


    @Update
    suspend fun update(product: ProductEntity)


    @Delete
    suspend fun delete(product: ProductEntity)


    @Query("SELECT SUM(price * quantity) FROM products")
    suspend fun totalInventory(): Double?
}