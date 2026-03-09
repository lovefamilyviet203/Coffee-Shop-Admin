package com.example.coffeeshopadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopadmin.domain.ItemsModel

class OrderItemsAdapter(
    private val items: List<ItemsModel>,
    private val context: Context
) : RecyclerView.Adapter<OrderItemsAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Viewholder(ViewholderOrderItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            itemNameTxt.text = item.title
            itemPriceTxt.text = "$${"%.2f".format(item.price)}"
            itemQtyTxt.text = "x${item.numberInCart}"
            itemSubtotalTxt.text = "$${"%.2f".format(item.price * item.numberInCart)}"

            if (item.picUrl.isNotEmpty() && item.picUrl[0].isNotEmpty()) {
                Glide.with(context).load(item.picUrl[0]).into(itemImg)
            }
        }
    }

    override fun getItemCount() = items.size
}