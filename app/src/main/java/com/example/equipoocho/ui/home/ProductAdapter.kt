package com.example.equipoocho.ui.home  // Defines the package for this class

import android.view.LayoutInflater  // Import for inflating layouts
import android.view.ViewGroup  // Import for handling the view group (parent container)
import androidx.recyclerview.widget.DiffUtil  // Import for using DiffUtil to calculate item differences
import androidx.recyclerview.widget.ListAdapter  // Import for the base adapter class for a list
import androidx.recyclerview.widget.RecyclerView  // Import for RecyclerView
import com.example.equipoocho.data.local.ProductEntity  // Import for the data class representing a product entity
import com.example.equipoocho.databinding.ItemProductBinding  // Import for binding the views defined in the XML layout
import com.example.equipoocho.ui.util.Formatters  // Import for custom formatting utilities

// Adapter for displaying a list of products
class ProductAdapter(private val onClick: (ProductEntity) -> Unit) :
    ListAdapter<ProductEntity, ProductAdapter.VH>(DIFF) {  // Extends ListAdapter, with ProductEntity and ViewHolder (VH) as generics

    // DiffUtil for efficiently handling changes in the list (such as adding or removing items)
    object DIFF : DiffUtil.ItemCallback<ProductEntity>() {
        // Compares whether two ProductEntity items are the same based on their IDs
        override fun areItemsTheSame(o: ProductEntity, n: ProductEntity) = o.id == n.id

        // Checks whether the contents of two ProductEntity items are the same
        override fun areContentsTheSame(o: ProductEntity, n: ProductEntity) = o == n
    }

    // ViewHolder class to hold the view references
    inner class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)  // The ViewHolder holds a reference to the ItemProductBinding

    // Called when a new ViewHolder is created. It inflates the layout and returns the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)  // Inflate the view from XML
        return VH(b)  // Return a new ViewHolder with the inflated binding
    }

    // Called to bind data to a ViewHolder at a particular position in the list
    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)  // Get the ProductEntity at the given position
        holder.b.tvName.text = p.name  // Set the product name in the TextView
        holder.b.tvId.text = p.id.toString()  // Set the product ID in the TextView
        holder.b.tvPrice.text = Formatters.money(p.price)  // Format and set the product price
        holder.b.root.setOnClickListener { onClick(p) }  // Set an onClick listener on the root view to invoke onClick when tapped
    }
}
