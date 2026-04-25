package com.plantify.plantify_app.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.plantify.plantify_app.R
import com.plantify.plantify_app.ui.home.HomeActivity
import com.plantify.plantify_app.ui.register.RegisterActivity
import kotlin.onFailure
import kotlin.onSuccess

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail    = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // 👁 Observar resultado del login
        viewModel.loginState.observe(this) { result ->
            result.onSuccess {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            result.onFailure {
                Toast.makeText(this,
                    "Credenciales incorrectas o usuario no registrado",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}