package com.example.equipoocho.ui.edit


import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.equipoocho.R
import com.example.equipoocho.data.local.ProductEntity
import com.example.equipoocho.data.repo.InventoryRepository
import com.example.equipoocho.databinding.ActivityEditProductBinding
import kotlinx.coroutines.launch


class EditProductActivity: AppCompatActivity() {
    private lateinit var b: ActivityEditProductBinding
    private lateinit var repo: InventoryRepository
    private var productId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(b.root)
        repo = InventoryRepository(this)


        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.edit_product)
        b.toolbar.setNavigationOnClickListener { finish() }


        productId = intent.getIntExtra("id", 0)
        lifecycleScope.launch {
            repo.product(productId)?.let { p ->
                b.tvId.text = p.id.toString()
                b.etName.setText(p.name)
                b.etPrice.setText(p.price.toString())
                b.etQty.setText(p.quantity.toString())
            }
        }


        b.etName.filters = arrayOf(InputFilter.LengthFilter(40))
        b.etPrice.inputType = InputType.TYPE_CLASS_NUMBER
        b.etQty.inputType = InputType.TYPE_CLASS_NUMBER


        val watcher = SimpleTextWatcher { validate() }
        b.etName.addTextChangedListener(watcher)
        b.etPrice.addTextChangedListener(watcher)
        b.etQty.addTextChangedListener(watcher)


        b.btnEdit.setOnClickListener {
            lifecycleScope.launch {
                val updated = ProductEntity(
                    id = productId,
                    name = b.etName.text.toString().trim(),
                    price = b.etPrice.text.toString().toDouble(),
                    quantity = b.etQty.text.toString().toInt()
                )
                repo.update(updated)
                finish()
            }
        }
        validate()
    }


    private fun validate() {
        b.btnEdit.isEnabled = b.etName.text!!.isNotBlank() &&
                b.etPrice.text!!.isNotBlank() && b.etQty.text!!.isNotBlank()
    }

    // dentro de EditProductActivity, al final:
    private class SimpleTextWatcher(private val onChanged: () -> Unit) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onChanged() }
        override fun afterTextChanged(s: android.text.Editable?) {}
    }

}