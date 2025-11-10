package com.example.equipoocho.ui.detail


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.equipoocho.R
import com.example.equipoocho.data.repo.InventoryRepository
import com.example.equipoocho.databinding.ActivityDetailProductBinding
import com.example.equipoocho.ui.edit.EditProductActivity
import com.example.equipoocho.ui.util.Formatters
import kotlinx.coroutines.launch


class ProductDetailActivity: AppCompatActivity() {
    private lateinit var b: ActivityDetailProductBinding
    private lateinit var repo: InventoryRepository
    private var productId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(b.root)
        repo = InventoryRepository(this)


        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_product)
        b.toolbar.setNavigationOnClickListener { finish() }


        productId = intent.getIntExtra("id", 0)
        lifecycleScope.launch { load() }


        b.btnDelete.setOnClickListener { confirmDelete() }
        b.fabEdit.setOnClickListener {
            startActivity(Intent(this, EditProductActivity::class.java).putExtra("id", productId))
        }
    }


    private suspend fun load() {
        val p = repo.product(productId) ?: return
        b.tvName.text = p.name
        b.tvUnitPrice.text = Formatters.money(p.price)
        b.tvQty.text = p.quantity.toString()
        val total = p.price * p.quantity
        b.tvTotal.text = Formatters.money(total)
    }


    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setMessage("¿Eliminar producto?")
            .setNegativeButton("No", null)
            .setPositiveButton("Sí") { _: DialogInterface, _: Int ->
                lifecycleScope.launch {
                    repo.product(productId)?.let { repo.remove(it) }
                    finish()
                }
            }.show()
    }
}