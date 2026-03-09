package com.example.coffeeshopadmin.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.coffeeshopadmin.R
import com.example.coffeeshopadmin.domain.ItemsModel
import com.google.firebase.database.FirebaseDatabase

class AddEditProductActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance()
    private var productKey: String? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val titleLabel = findViewById<TextView>(R.id.titleTxt)
        val productImg = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.productImg)
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val priceInput = findViewById<EditText>(R.id.priceInput)
        val categoryInput = findViewById<EditText>(R.id.categoryInput)
        val ratingInput = findViewById<EditText>(R.id.ratingInput)
        val imageUrlInput = findViewById<EditText>(R.id.imageUrlInput)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val deleteBtn = findViewById<Button>(R.id.deleteBtn)
        val errorTxt = findViewById<TextView>(R.id.errorTxt)
        val changeImageBtn = findViewById<Button>(R.id.changeImageBtn)

        backBtn.setOnClickListener { finish() }

        // Check if editing existing product
        productKey = intent.getStringExtra("productKey")
        if (productKey != null) {
            isEditMode = true
            titleLabel.text = "Chỉnh sửa sản phẩm"
            saveBtn.text = "Cập nhật sản phẩm"
            deleteBtn.visibility = View.VISIBLE

            // Pre-fill fields
            titleInput.setText(intent.getStringExtra("title"))
            priceInput.setText(intent.getDoubleExtra("price", 0.0).toString())
            categoryInput.setText(intent.getStringExtra("categoryId"))
            ratingInput.setText(intent.getDoubleExtra("rating", 0.0).toString())
            descriptionInput.setText(intent.getStringExtra("description"))
            val imageUrl = intent.getStringArrayListExtra("picUrl")?.firstOrNull() ?: ""
            imageUrlInput.setText(imageUrl)
            if (imageUrl.isNotEmpty()) {
                Glide.with(this).load(imageUrl).into(productImg)
            }
        }

        changeImageBtn.setOnClickListener {
            val url = imageUrlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                Glide.with(this).load(url).into(productImg)
            }
        }

        imageUrlInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = imageUrlInput.text.toString().trim()
                if (url.isNotEmpty()) Glide.with(this).load(url).into(productImg)
            }
        }

        saveBtn.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val priceStr = priceInput.text.toString().trim()
            val categoryStr = categoryInput.text.toString().trim()
            val ratingStr = ratingInput.text.toString().trim()
            val imageUrl = imageUrlInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (title.isEmpty() || priceStr.isEmpty()) {
                errorTxt.text = "Vui lòng nhập tên và giá sản phẩm"
                errorTxt.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0
            val rating = ratingStr.toDoubleOrNull() ?: 4.5
            val categoryId = categoryStr.ifEmpty { "0" }
            val picUrls = if (imageUrl.isNotEmpty()) listOf(imageUrl) else emptyList()

            val item = ItemsModel(
                title = title,
                description = description,
                picUrl = picUrls,
                price = price,
                rating = rating,
                categoryId = categoryId
            )

            saveBtn.isEnabled = false
            errorTxt.visibility = View.GONE

            val ref = if (isEditMode && productKey != null) {
                db.getReference("Items").child(productKey!!)
            } else {
                db.getReference("Items").push()
            }

            ref.setValue(item)
                .addOnSuccessListener { finish() }
                .addOnFailureListener {
                    saveBtn.isEnabled = true
                    errorTxt.text = "Lỗi: ${it.message}"
                    errorTxt.visibility = View.VISIBLE
                }
        }

        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này không?")
                .setPositiveButton("Xóa") { _, _ ->
                    productKey?.let { key ->
                        db.getReference("Items").child(key).removeValue()
                            .addOnSuccessListener { finish() }
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }
}
