package com.plantify.plantify_app.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.plantify.plantify_app.R
import com.plantify.plantify_app.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val etEmail    = findViewById<TextInputEditText>(R.id.etEmailRegister)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordRegister)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(email, password)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // 👁 Observar resultado del registro
        viewModel.registerState.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            result.onFailure { e ->
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}