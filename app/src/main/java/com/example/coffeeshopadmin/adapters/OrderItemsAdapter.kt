package com.example.coffeeshopadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.domain.ItemsModel
import com.google.android.material.imageview.ShapeableImageView

class OrderItemsAdapter(
    private val items: List<ItemsModel>,
    private val context: Context
) : RecyclerView.Adapter<OrderItemsAdapter.Viewholder>() {

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImg: ShapeableImageView = itemView.findViewById(R.id.itemImg)
        val itemNameTxt: TextView = itemView.findViewById(R.id.itemNameTxt)
        val itemQtyTxt: TextView = itemView.findViewById(R.id.itemQtyTxt)
        val itemPriceTxt: TextView = itemView.findViewById(R.id.itemPriceTxt)
        val itemSubtotalTxt: TextView = itemView.findViewById(R.id.itemSubtotalTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Viewholder(LayoutInflater.from(context).inflate(R.layout.viewholder_order_item, parent, false))

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.itemNameTxt.text = item.title
        holder.itemPriceTxt.text = "$${"%.2f".format(item.price)}"
        holder.itemQtyTxt.text = "x${item.numberInCart}"
        holder.itemSubtotalTxt.text = "$${"%.2f".format(item.price * item.numberInCart)}"

        if (item.picUrl.isNotEmpty() && item.picUrl[0].isNotEmpty()) {
            Glide.with(context).load(item.picUrl[0]).into(holder.itemImg)
        }
    }

    override fun getItemCount() = items.size
}
