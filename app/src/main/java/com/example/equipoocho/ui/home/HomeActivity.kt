package com.example.equipoocho.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.equipoocho.databinding.ActivityHomeBinding
import com.example.equipoocho.ui.add.AddProductActivity
import com.example.equipoocho.ui.detail.ProductDetailActivity
import com.example.equipoocho.ui.login.SessionManager
import com.example.equipoocho.R
import com.example.equipoocho.data.local.ProductEntity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val vm: HomeViewModel by viewModels()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)

        // Toolbar
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.inventory)

        // Logout icon
        binding.btnLogout.setOnClickListener {
            session.setLogged(false)
            finish() // Al volver atrás, regresa a LoginActivity
        }

        // Recycler
        val adapter = ProductAdapter(onClick = { product ->
            startActivity(Intent(this, ProductDetailActivity::class.java).putExtra("id", product.id))
        })
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        // Progress naranja mientras carga
        binding.progress.visibility = android.view.View.VISIBLE
        vm.products.observe(this) { list: List<ProductEntity> ->
            binding.progress.visibility = android.view.View.GONE
            adapter.submitList(list)
        }

        // FAB agregar
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        // Migración a OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this) {
            moveTaskToBack(true) // Mueve la actividad a segundo plano, no regresa a Login
        }
    }
}
