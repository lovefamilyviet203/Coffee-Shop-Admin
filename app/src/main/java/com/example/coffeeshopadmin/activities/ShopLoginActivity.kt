package com.example.coffeeshopadmin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeshopadmin.R
import com.google.firebase.auth.FirebaseAuth

class ShopLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_login)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val errorTxt = findViewById<TextView>(R.id.errorTxt)

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                errorTxt.text = "Vui lòng nhập đầy đủ email và mật khẩu"
                errorTxt.visibility = View.VISIBLE
                return@setOnClickListener
            }

            loginBtn.isEnabled = false
            errorTxt.visibility = View.GONE

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    loginBtn.isEnabled = true
                    errorTxt.text = "Đăng nhập thất bại: ${it.message}"
                    errorTxt.visibility = View.VISIBLE
                }
        }
    }
}
