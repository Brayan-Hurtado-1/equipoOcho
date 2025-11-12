package com.example.equipoocho.ui.detail

// Importaciones necesarias para trabajar con Android y la base de datos
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

// Actividad para mostrar el detalle de un producto específico
class ProductDetailActivity: AppCompatActivity() {

    // Vinculación de la vista
    private lateinit var b: ActivityDetailProductBinding
    // Repositorio de inventario
    private lateinit var repo: InventoryRepository
    // ID del producto a mostrar
    private var productId: Int = 0

    // Método llamado cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout y vincular la vista
        b = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(b.root)
        
        // Inicializar el repositorio
        repo = InventoryRepository(this)

        // Configuración de la barra de acción
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Habilitar el botón de regreso
        supportActionBar?.title = getString(R.string.detail_product)  // Título de la actividad
        b.toolbar.setNavigationOnClickListener { finish() }  // Volver a la actividad anterior cuando se presiona la flecha

        // Obtener el ID del producto desde los extras del Intent
        productId = intent.getIntExtra("id", 0)

        // Cargar los detalles del producto de forma asíncrona usando corutinas
        lifecycleScope.launch { load() }

        // Configurar el botón de eliminación
        b.btnDelete.setOnClickListener { confirmDelete() }

        // Configurar el botón de edición
        b.fabEdit.setOnClickListener {
            startActivity(Intent(this, EditProductActivity::class.java).putExtra("id", productId))  // Abre la actividad para editar el producto
        }
    }

    // Función suspendida para cargar los detalles del producto desde la base de datos
    private suspend fun load() {
        val p = repo.product(productId) ?: return  // Obtener el producto por ID
        // Actualizar los campos de la vista con la información del producto
        b.tvName.text = p.name
        b.tvUnitPrice.text = Formatters.money(p.price)  // Formatear el precio
        b.tvQty.text = p.quantity.toString()  // Mostrar la cantidad
        val total = p.price * p.quantity  // Calcular el total (precio * cantidad)
        b.tvTotal.text = Formatters.money(total)  // Formatear el total
    }

    // Función para confirmar la eliminación del producto
    private fun confirmDelete() {
        // Crear un cuadro de diálogo para confirmar la eliminación
        AlertDialog.Builder(this)
            .setMessage("¿Eliminar producto?")  // Mensaje del diálogo
            .setNegativeButton("No", null)  // Acción para cancelar (no hacer nada)
            .setPositiveButton("Sí") { _: DialogInterface, _: Int ->  // Acción para confirmar la eliminación
                lifecycleScope.launch {
                    // Eliminar el producto si existe
                    repo.product(productId)?.let { repo.remove(it) }
                    finish()  // Finalizar la actividad después de eliminar el producto
                }
            }.show()  // Mostrar el cuadro de diálogo
    }
}
