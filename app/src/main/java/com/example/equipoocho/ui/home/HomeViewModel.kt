package com.example.equipoocho.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData   // <-- Asegúrate de agregar esta importación
import com.example.equipoocho.data.local.ProductEntity
import com.example.equipoocho.data.repo.InventoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = InventoryRepository(app)
    val products = repo.products().asLiveData()  // Convierte el Flow a LiveData
}
