package com.example.coffeeshopadmin.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.adapters.OrderItemsAdapter
import com.example.coffeeshopadmin.domain.ItemsModel
import com.example.coffeeshopadmin.domain.OrderModel
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShopOrderDetailActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_order_detail)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val orderIdTxt = findViewById<TextView>(R.id.orderIdTxt)
        val customerNameTxt = findViewById<TextView>(R.id.customerNameTxt)
        val customerEmailTxt = findViewById<TextView>(R.id.customerEmailTxt)
        val addressTxt = findViewById<TextView>(R.id.addressTxt)
        val paymentTxt = findViewById<TextView>(R.id.paymentTxt)
        val dateTimeTxt = findViewById<TextView>(R.id.dateTimeTxt)
        val statusTxt = findViewById<TextView>(R.id.statusTxt)
        val orderItemsView = findViewById<RecyclerView>(R.id.orderItemsView)
        val subtotalTxt = findViewById<TextView>(R.id.subtotalTxt)
        val deliveryFeeTxt = findViewById<TextView>(R.id.deliveryFeeTxt)
        val taxTxt = findViewById<TextView>(R.id.taxTxt)
        val grandTotalTxt = findViewById<TextView>(R.id.grandTotalTxt)

        val pendingBtn = findViewById<Button>(R.id.pendingBtn)
        val processingBtn = findViewById<Button>(R.id.processingBtn)
        val shippingBtn = findViewById<Button>(R.id.shippingBtn)
        val deliveredBtn = findViewById<Button>(R.id.deliveredBtn)

        backBtn.setOnClickListener { finish() }
        orderItemsView.layoutManager = LinearLayoutManager(this)

        val orderId = intent.getStringExtra("orderId") ?: return

        db.getReference("Orders").child(orderId).get()
            .addOnSuccessListener { snapshot ->
                val order = snapshot.getValue(OrderModel::class.java) ?: return@addOnSuccessListener

                orderIdTxt.text = "#${order.orderId.takeLast(8).uppercase()}"
                customerNameTxt.text = order.userName.ifEmpty { "---" }
                customerEmailTxt.text = order.userEmail.ifEmpty { "---" }
                addressTxt.text = order.address.ifEmpty { "---" }
                paymentTxt.text = order.paymentMethod.ifEmpty { "---" }
                dateTimeTxt.text = order.dateTime.ifEmpty { "---" }
                statusTxt.text = order.status

                val items = order.items
                orderItemsView.adapter = OrderItemsAdapter(items, this)

                val subtotal = items.sumOf { it.price * it.numberInCart }
                subtotalTxt.text = "$${"%.2f".format(subtotal)}"
                deliveryFeeTxt.text = "$${"%.2f".format(order.deliveryFee)}"
                taxTxt.text = "$${"%.2f".format(order.tax)}"
                grandTotalTxt.text = "$${"%.2f".format(order.grandTotal)}"
            }

        fun updateStatus(newStatus: String) {
            db.getReference("Orders").child(orderId).child("status").setValue(newStatus)
                .addOnSuccessListener { statusTxt.text = newStatus }
        }

        pendingBtn.setOnClickListener { updateStatus("Pending") }
        processingBtn.setOnClickListener { updateStatus("Processing") }
        shippingBtn.setOnClickListener { updateStatus("Shipping") }
        deliveredBtn.setOnClickListener { updateStatus("Delivered") }
    }
}
