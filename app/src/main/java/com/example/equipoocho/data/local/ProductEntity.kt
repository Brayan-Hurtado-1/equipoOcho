package com.example.equipoocho.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int, // Código de 4 dígitos
    val name: String,
    val price: Double, // Precio por unidad
    val quantity: Int // Cantidad disponible
)