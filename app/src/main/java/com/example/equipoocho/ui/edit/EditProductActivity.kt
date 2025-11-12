package com.example.equipoocho.ui.edit  // Defines the package for this activity

import android.os.Bundle  // Import for working with the activity lifecycle (onCreate, etc.)
import android.text.InputFilter  // Import for setting input filters (e.g., limiting text length)
import android.text.InputType  // Import for defining the type of input (e.g., number input)
import androidx.appcompat.app.AppCompatActivity  // Import for the base activity class with AppCompat support
import androidx.lifecycle.lifecycleScope  // Import for managing coroutines tied to the lifecycle of this activity
import com.example.equipoocho.R  // Import for accessing resources (e.g., strings, layouts)
import com.example.equipoocho.data.local.ProductEntity  // Import for the ProductEntity class that represents a product in the database
import com.example.equipoocho.data.repo.InventoryRepository  // Import for accessing the repository to interact with the data layer
import com.example.equipoocho.databinding.ActivityEditProductBinding  // Import for binding views using ViewBinding
import kotlinx.coroutines.launch  // Import for launching coroutines

// EditProductActivity allows editing of product details
class EditProductActivity: AppCompatActivity() {
    private lateinit var b: ActivityEditProductBinding  // ViewBinding reference to the activity's views
    private lateinit var repo: InventoryRepository  // Reference to the InventoryRepository for database access
    private var productId = 0  // Variable to store the product ID to be edited

    // onCreate is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inflate the activity's layout using ViewBinding
        b = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(b.root)  // Set the inflated layout as the content view
        
        // Initialize the repository to access data
        repo = InventoryRepository(this)

        // Set up the toolbar
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Show the back button in the toolbar
        supportActionBar?.title = getString(R.string.edit_product)  // Set the toolbar title to "Edit Product"
        b.toolbar.setNavigationOnClickListener { finish() }  // Set the back button action to finish the activity

        // Get the product ID passed from the previous activity
        productId = intent.getIntExtra("id", 0)  // Retrieve the product ID from the Intent
        
        // Launch a coroutine to fetch the product details and populate the views
        lifecycleScope.launch {
            repo.product(productId)?.let { p ->
                // Populate the views with the fetched product details
                b.tvId.text = p.id.toString()
                b.etName.setText(p.name)
                b.etPrice.setText(p.price.toString())
                b.etQty.setText(p.quantity.toString())
            }
        }

        // Set input filters and input types for the fields
        b.etName.filters = arrayOf(InputFilter.LengthFilter(40))  // Limit the length of the name to 40 characters
        b.etPrice.inputType = InputType.TYPE_CLASS_NUMBER  // Set price input to accept only numbers
        b.etQty.inputType = InputType.TYPE_CLASS_NUMBER  // Set quantity input to accept only numbers

        // Create a text watcher to validate the form whenever the text changes
        val watcher = SimpleTextWatcher { validate() }
        b.etName.addTextChangedListener(watcher)  // Add the watcher to the name input field
        b.etPrice.addTextChangedListener(watcher)  // Add the watcher to the price input field
        b.etQty.addTextChangedListener(watcher)  // Add the watcher to the quantity input field

        // Set the onClickListener for the "Edit" button to save the updated product data
        b.btnEdit.setOnClickListener {
            lifecycleScope.launch {
                val updated = ProductEntity(
                    id = productId,  // Use the existing product ID
                    name = b.etName.text.toString().trim(),  // Get the updated name and trim any whitespace
                    price = b.etPrice.text.toString().toDouble(),  // Get the updated price and convert it to a double
                    quantity = b.etQty.text.toString().toInt()  // Get the updated quantity and convert it to an integer
                )
                repo.update(updated)  // Update the product in the database
                finish()  // Close the activity after the update
            }
        }

        // Call validate to enable or disable the "Edit" button based on input fields
        validate()
    }

    // Validation function to enable the "Edit" button only if all fields are non-empty
    private fun validate() {
        b.btnEdit.isEnabled = b.etName.text!!.isNotBlank() &&  // Check if name is not blank
                b.etPrice.text!!.isNotBlank() &&  // Check if price is not blank
                b.etQty.text!!.isNotBlank()  // Check if quantity is not blank
    }

    // A custom text watcher to trigger validation when the text in any field changes
    private class SimpleTextWatcher(private val onChanged: () -> Unit) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}  // Not used
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onChanged() }  // Call the validate function when text changes
        override fun afterTextChanged(s: android.text.Editable?) {}  // Not used
    }
}
