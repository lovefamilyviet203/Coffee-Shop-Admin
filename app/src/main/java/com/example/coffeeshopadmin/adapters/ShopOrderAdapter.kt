package com.example.coffeeshopadmin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.activities.ShopOrderDetailActivity
import com.example.coffeeshopadmin.domain.OrderModel

class ShopOrderAdapter(
    private var orders: MutableList<OrderModel>,
    private val context: Context
) : RecyclerView.Adapter<ShopOrderAdapter.Viewholder>() {

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTxt: TextView = itemView.findViewById(R.id.orderIdTxt)
        val customerNameTxt: TextView = itemView.findViewById(R.id.customerNameTxt)
        val dateTimeTxt: TextView = itemView.findViewById(R.id.dateTimeTxt)
        val totalTxt: TextView = itemView.findViewById(R.id.totalTxt)
        val statusTxt: TextView = itemView.findViewById(R.id.statusTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Viewholder(LayoutInflater.from(context).inflate(R.layout.viewholder_shop_order, parent, false))

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val order = orders[position]
        holder.orderIdTxt.text = "#${order.orderId.takeLast(8).uppercase()}"
        holder.customerNameTxt.text = order.userName.ifEmpty { order.userEmail }
        holder.dateTimeTxt.text = order.dateTime
        holder.totalTxt.text = "$${"%.2f".format(order.grandTotal)}"
        holder.statusTxt.text = order.status

        val statusColor = when (order.status) {
            "Delivered" -> android.graphics.Color.parseColor("#4CAF50")
            "Shipping" -> android.graphics.Color.parseColor("#2196F3")
            "Processing" -> android.graphics.Color.parseColor("#FF9800")
            else -> context.resources.getColor(R.color.lightBrown, null)
        }
        holder.statusTxt.setTextColor(statusColor)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShopOrderDetailActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = orders.size

    fun updateList(newList: MutableList<OrderModel>) {
        orders = newList
        notifyDataSetChanged()
    }
}
