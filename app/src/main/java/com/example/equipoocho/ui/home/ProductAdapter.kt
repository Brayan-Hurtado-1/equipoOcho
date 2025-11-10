package com.example.equipoocho.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.equipoocho.data.local.ProductEntity
import com.example.equipoocho.databinding.ItemProductBinding
import com.example.equipoocho.ui.util.Formatters


class ProductAdapter(private val onClick: (ProductEntity) -> Unit) :
    ListAdapter<ProductEntity, ProductAdapter.VH>(DIFF) {


    object DIFF : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(o: ProductEntity, n: ProductEntity) = o.id == n.id
        override fun areContentsTheSame(o: ProductEntity, n: ProductEntity) = o == n
    }


    inner class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        holder.b.tvName.text = p.name
        holder.b.tvId.text = p.id.toString()
        holder.b.tvPrice.text = Formatters.money(p.price)
        holder.b.root.setOnClickListener { onClick(p) }
    }
}