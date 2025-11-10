package com.example.equipoocho.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.equipoocho.R
import com.example.equipoocho.data.local.ProductEntity
import com.example.equipoocho.data.repo.InventoryRepository
import com.example.equipoocho.databinding.ActivityAddProductBinding
import kotlinx.coroutines.launch

class AddProductActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddProductBinding
    private lateinit var repo: InventoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(b.root)
        repo = InventoryRepository(this)

        // Toolbar
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_product)
        b.toolbar.setNavigationOnClickListener { finish() }

        // Reglas de input
        b.etCode.inputType = InputType.TYPE_CLASS_NUMBER
        b.etCode.filters = arrayOf(InputFilter.LengthFilter(4))
        b.etName.filters = arrayOf(InputFilter.LengthFilter(40))
        b.etPrice.inputType = InputType.TYPE_CLASS_NUMBER
        b.etPrice.filters = arrayOf(InputFilter.LengthFilter(20))
        b.etQty.inputType = InputType.TYPE_CLASS_NUMBER
        b.etQty.filters = arrayOf(InputFilter.LengthFilter(4))

        // Validación habilitar botón
        val watcher = SimpleTextWatcher { validate() }
        b.etCode.addTextChangedListener(watcher)
        b.etName.addTextChangedListener(watcher)
        b.etPrice.addTextChangedListener(watcher)
        b.etQty.addTextChangedListener(watcher)

        b.btnSave.setOnClickListener {
            val id = b.etCode.text.toString().toInt()
            val name = b.etName.text.toString().trim()
            val price = b.etPrice.text.toString().toDouble()
            val qty = b.etQty.text.toString().toInt()
            lifecycleScope.launch {
                try {
                    repo.add(ProductEntity(id, name, price, qty))
                    Toast.makeText(this@AddProductActivity, "Guardado", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@AddProductActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validate() {
        b.btnSave.isEnabled =
            b.etCode.text!!.isNotBlank() &&
                    b.etName.text!!.isNotBlank() &&
                    b.etPrice.text!!.isNotBlank() &&
                    b.etQty.text!!.isNotBlank()
    }

    // Clase interna: evita imports fuera de lugar
    private class SimpleTextWatcher(private val onChanged: () -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onChanged() }
        override fun afterTextChanged(s: Editable?) {}
    }
}
