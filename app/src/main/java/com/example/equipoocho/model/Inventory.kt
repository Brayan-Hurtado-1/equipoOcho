package com.example.equipoOcho.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Inventory(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val name: String,
    val price: Int,
    val quantity: Int= 0): Serializable
