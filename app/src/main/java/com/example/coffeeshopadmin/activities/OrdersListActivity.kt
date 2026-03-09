package com.example.coffeeshopadmin.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.adapters.ShopOrderAdapter
import com.example.coffeeshopadmin.domain.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersListActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance()
    private val allOrders = mutableListOf<OrderModel>()
    private lateinit var adapter: ShopOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)

        val backBtn = findViewById<View>(R.id.backBtn)
        val ordersView = findViewById<RecyclerView>(R.id.ordersView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val filterAll = findViewById<TextView>(R.id.filterAll)
        val filterPending = findViewById<TextView>(R.id.filterPending)
        val filterProcessing = findViewById<TextView>(R.id.filterProcessing)
        val filterShipping = findViewById<TextView>(R.id.filterShipping)
        val filterDelivered = findViewById<TextView>(R.id.filterDelivered)

        ordersView.layoutManager = LinearLayoutManager(this)
        backBtn.setOnClickListener { finish() }

        val filterBtns = listOf(filterAll, filterPending, filterProcessing, filterShipping, filterDelivered)
        val filterMap = mapOf(
            filterAll to "", filterPending to "Pending",
            filterProcessing to "Processing", filterShipping to "Shipping",
            filterDelivered to "Delivered"
        )

        fun setActiveFilter(active: TextView) {
            filterBtns.forEach {
                it.setBackgroundResource(R.drawable.brown_2_full_corner_bg)
                it.setTextColor(resources.getColor(R.color.lightBrown, null))
            }
            active.setBackgroundResource(R.drawable.brown_full_corner_bg)
            active.setTextColor(resources.getColor(R.color.white, null))
        }

        filterBtns.forEach { btn ->
            btn.setOnClickListener {
                setActiveFilter(btn)
                filterOrders(filterMap[btn] ?: "", ordersView)
            }
        }
        setActiveFilter(filterAll)

        loadOrders(ordersView, progressBar)
    }

    override fun onResume() {
        super.onResume()
        loadOrders(findViewById(R.id.ordersView), findViewById(R.id.progressBar))
    }

    private fun loadOrders(recyclerView: RecyclerView, progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        db.getReference("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = View.GONE
                allOrders.clear()
                snapshot.children.forEach { child ->
                    child.getValue(OrderModel::class.java)?.let { allOrders.add(it) }
                }
                allOrders.sortByDescending { it.timestamp }
                adapter = ShopOrderAdapter(allOrders.toMutableList(), this@OrdersListActivity)
                recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun filterOrders(status: String, recyclerView: RecyclerView) {
        if (!::adapter.isInitialized) return
        val filtered = if (status.isEmpty()) allOrders.toMutableList()
        else allOrders.filter { it.status == status }.toMutableList()
        adapter.updateList(filtered)
    }
}
