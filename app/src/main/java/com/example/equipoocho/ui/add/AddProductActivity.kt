package com.example.equipoocho.ui.add  // Defines the package for this activity

import android.os.Bundle  // Import for working with the activity lifecycle (onCreate, etc.)
import android.text.Editable  // Import for handling the editable text in the EditText fields
import android.text.InputFilter  // Import for applying input filters on text fields
import android.text.InputType  // Import for defining input types (e.g., number input)
import android.text.TextWatcher  // Import for adding a listener to text changes
import android.widget.Toast  // Import for showing Toast messages
import androidx.appcompat.app.AppCompatActivity  // Import for the base activity class with AppCompat support
import androidx.lifecycle.lifecycleScope  // Import for managing coroutines tied to the lifecycle of this activity
import com.example.equipoocho.R  // Import for accessing resources (e.g., strings, layouts)
import com.example.equipoocho.data.local.ProductEntity  // Import for the ProductEntity class representing a product in the database
import com.example.equipoocho.data.repo.InventoryRepository  // Import for accessing the repository to interact with the data layer
import com.example.equipoocho.databinding.ActivityAddProductBinding  // Import for binding the views using ViewBinding
import kotlinx.coroutines.launch  // Import for launching coroutines

// AddProductActivity handles adding a new product to the inventory
class AddProductActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddProductBinding  // ViewBinding reference to the activity's views
    private lateinit var repo: InventoryRepository  // Reference to the InventoryRepository for database access

    // onCreate is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        b = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(b.root)  // Set the inflated layout as the content view

        // Initialize the repository to access data
        repo = InventoryRepository(this)

        // Setup the toolbar
        setSupportActionBar(b.toolbar)  // Set the custom toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Enable the back button in the toolbar
        supportActionBar?.title = getString(R.string.add_product)  // Set the toolbar title to "Add Product"
        b.toolbar.setNavigationOnClickListener { finish() }  // Close the activity when the back button is pressed

        // Set input rules for each EditText field
        b.etCode.inputType = InputType.TYPE_CLASS_NUMBER  // Set input type to accept numbers for product code
        b.etCode.filters = arrayOf(InputFilter.LengthFilter(4))  // Limit the length of the product code to 4 characters
        b.etName.filters = arrayOf(InputFilter.LengthFilter(40))  // Limit the length of the product name to 40 characters
        b.etPrice.inputType = InputType.TYPE_CLASS_NUMBER  // Set input type to accept numbers for price
        b.etPrice.filters = arrayOf(InputFilter.LengthFilter(20))  // Limit the length of the price to 20 characters
        b.etQty.inputType = InputType.TYPE_CLASS_NUMBER  // Set input type to accept numbers for quantity
        b.etQty.filters = arrayOf(InputFilter.LengthFilter(4))  // Limit the length of the quantity to 4 characters

        // Create a text watcher to enable or disable the save button based on input
        val watcher = SimpleTextWatcher { validate() }
        b.etCode.addTextChangedListener(watcher)  // Add the watcher to the product code input field
        b.etName.addTextChangedListener(watcher)  // Add the watcher to the product name input field
        b.etPrice.addTextChangedListener(watcher)  // Add the watcher to the price input field
        b.etQty.addTextChangedListener(watcher)  // Add the watcher to the quantity input field

        // Set the onClickListener for the "Save" button to save the new product
        b.btnSave.setOnClickListener {
            // Retrieve the values from the input fields and convert them to appropriate types
            val id = b.etCode.text.toString().toInt()  // Convert product code to integer
            val name = b.etName.text.toString().trim()  // Get the product name and trim any whitespace
            val price = b.etPrice.text.toString().toDouble()  // Convert price to double
            val qty = b.etQty.text.toString().toInt()  // Convert quantity to integer

            // Launch a coroutine to add the new product in the database
            lifecycleScope.launch {
                try {
                    // Create a new ProductEntity object with the provided details
                    repo.add(ProductEntity(id, name, price, qty))
                    Toast.makeText(this@AddProductActivity, "Guardado", Toast.LENGTH_SHORT).show()  // Show success message
                    finish()  // Close the activity after saving
                } catch (e: Exception) {
                    // Show an error message if something goes wrong
                    Toast.makeText(this@AddProductActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Validation function to enable the "Save" button only if all fields are non-empty
    private fun validate() {
        b.btnSave.isEnabled =
            b.etCode.text!!.isNotBlank() &&  // Ensure product code is not blank
                    b.etName.text!!.isNotBlank() &&  // Ensure product name is not blank
                    b.etPrice.text!!.isNotBlank() &&  // Ensure product price is not blank
                    b.etQty.text!!.isNotBlank()  // Ensure product quantity is not blank
    }

    // Custom text watcher class to trigger validation when text changes
    private class SimpleTextWatcher(private val onChanged: () -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onChanged() }  // Call validation on text change
        override fun afterTextChanged(s: Editable?) {}
    }
}
