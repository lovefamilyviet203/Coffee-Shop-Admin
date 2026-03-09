package com.example.coffeeshopadmin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.activities.AddEditProductActivity
import com.example.coffeeshopadmin.domain.ItemsModel
import com.google.android.material.imageview.ShapeableImageView

class ShopProductAdapter(
    private var items: MutableList<ItemsModel>,
    private val context: Context
) : RecyclerView.Adapter<ShopProductAdapter.Viewholder>() {

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImg: ShapeableImageView = itemView.findViewById(R.id.productImg)
        val productNameTxt: TextView = itemView.findViewById(R.id.productNameTxt)
        val productCategoryTxt: TextView = itemView.findViewById(R.id.productCategoryTxt)
        val productPriceTxt: TextView = itemView.findViewById(R.id.productPriceTxt)
        val productRatingTxt: TextView = itemView.findViewById(R.id.productRatingTxt)
        val editBtn: ImageView = itemView.findViewById(R.id.editBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Viewholder(LayoutInflater.from(context).inflate(R.layout.viewholder_product, parent, false))

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.productNameTxt.text = item.title
        holder.productCategoryTxt.text = "Danh mục: ${item.categoryId}"
        holder.productPriceTxt.text = "$${"%.2f".format(item.price)}"
        holder.productRatingTxt.text = item.rating.toString()

        if (item.picUrl.isNotEmpty() && item.picUrl[0].isNotEmpty()) {
            Glide.with(context).load(item.picUrl[0]).into(holder.productImg)
        }

        val openEdit = {
            val intent = Intent(context, AddEditProductActivity::class.java).apply {
                putExtra("productKey", item.extra)
                putExtra("title", item.title)
                putExtra("price", item.price)
                putExtra("categoryId", item.categoryId)
                putExtra("rating", item.rating)
                putExtra("description", item.description)
                putStringArrayListExtra("picUrl", ArrayList(item.picUrl))
            }
            context.startActivity(intent)
        }

        holder.editBtn.setOnClickListener { openEdit() }
        holder.itemView.setOnClickListener { openEdit() }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: MutableList<ItemsModel>) {
        items = newList
        notifyDataSetChanged()
    }
}
