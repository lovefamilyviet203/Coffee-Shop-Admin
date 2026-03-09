package com.example.coffeeshopadmin.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.adapters.ShopProductAdapter
import com.example.coffeeshopadmin.domain.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductsListActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance()
    private val allProducts = mutableListOf<ItemsModel>()
    private lateinit var adapter: ShopProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_list)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val addProductBtn = findViewById<ImageView>(R.id.addProductBtn)
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val productsView = findViewById<RecyclerView>(R.id.productsView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        productsView.layoutManager = LinearLayoutManager(this)

        backBtn.setOnClickListener { finish() }
        addProductBtn.setOnClickListener {
            startActivity(Intent(this, AddEditProductActivity::class.java))
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadProducts(productsView, progressBar)
    }

    override fun onResume() {
        super.onResume()
        loadProducts(
            findViewById(R.id.productsView),
            findViewById(R.id.progressBar)
        )
    }

    private fun loadProducts(recyclerView: RecyclerView, progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        db.getReference("Items").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = View.GONE
                allProducts.clear()
                snapshot.children.forEach { child ->
                    child.getValue(ItemsModel::class.java)?.let {
                        it.extra = child.key ?: ""
                        allProducts.add(it)
                    }
                }
                adapter = ShopProductAdapter(allProducts.toMutableList(), this@ProductsListActivity)
                recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun filterProducts(query: String) {
        if (!::adapter.isInitialized) return
        val filtered = if (query.isEmpty()) {
            allProducts.toMutableList()
        } else {
            allProducts.filter { it.title.contains(query, ignoreCase = true) }.toMutableList()
        }
        adapter.updateList(filtered)
    }
}
