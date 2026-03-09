package com.example.coffeeshopadmin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.adapters.ShopOrderAdapter
import com.example.coffeeshopadmin.domain.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        val logoutBtn = findViewById<ImageView>(R.id.logoutBtn)
        val adminNameTxt = findViewById<TextView>(R.id.adminNameTxt)
        val totalOrdersTxt = findViewById<TextView>(R.id.totalOrdersTxt)
        val totalProductsTxt = findViewById<TextView>(R.id.totalProductsTxt)
        val revenueTxt = findViewById<TextView>(R.id.revenueTxt)
        val recentOrdersView = findViewById<RecyclerView>(R.id.recentOrdersView)
        val progressBarOrders = findViewById<View>(R.id.progressBarOrders)
        val manageProductsBtn = findViewById<LinearLayout>(R.id.manageProductsBtn)
        val manageOrdersBtn = findViewById<LinearLayout>(R.id.manageOrdersBtn)
        val navProducts = findViewById<LinearLayout>(R.id.navProducts)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val seeAllOrdersBtn = findViewById<TextView>(R.id.seeAllOrdersBtn)

        auth.currentUser?.let {
            adminNameTxt.text = it.email?.substringBefore("@") ?: "Admin"
        }

        recentOrdersView.layoutManager = LinearLayoutManager(this)

        logoutBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, ShopLoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        manageProductsBtn.setOnClickListener { startActivity(Intent(this, ProductsListActivity::class.java)) }
        navProducts.setOnClickListener { startActivity(Intent(this, ProductsListActivity::class.java)) }
        manageOrdersBtn.setOnClickListener { startActivity(Intent(this, OrdersListActivity::class.java)) }
        navOrders.setOnClickListener { startActivity(Intent(this, OrdersListActivity::class.java)) }
        seeAllOrdersBtn.setOnClickListener { startActivity(Intent(this, OrdersListActivity::class.java)) }

        loadStats(totalOrdersTxt, totalProductsTxt, revenueTxt)
        loadRecentOrders(recentOrdersView, progressBarOrders)
    }

    private fun loadStats(ordersTxt: TextView, productsTxt: TextView, revenueTxt: TextView) {
        db.getReference("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<OrderModel>()
                snapshot.children.forEach { child ->
                    child.getValue(OrderModel::class.java)?.let { orders.add(it) }
                }
                ordersTxt.text = orders.size.toString()
                val revenue = orders.sumOf { it.grandTotal }
                revenueTxt.text = "$${"%.0f".format(revenue)}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        db.getReference("Items").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsTxt.text = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadRecentOrders(recyclerView: RecyclerView, progressBar: View) {
        progressBar.visibility = View.VISIBLE
        db.getReference("Orders").orderByChild("timestamp").limitToLast(5)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressBar.visibility = View.GONE
                    val orders = mutableListOf<OrderModel>()
                    snapshot.children.forEach { child ->
                        child.getValue(OrderModel::class.java)?.let { orders.add(it) }
                    }
                    orders.sortByDescending { it.timestamp }
                    recyclerView.adapter = ShopOrderAdapter(orders, this@DashboardActivity)
                }
                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                }
            })
    }
}
