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

        val etNombre          = findViewById<TextInputEditText>(R.id.etNombreRegister)
        val etApellido = findViewById<TextInputEditText>(R.id.etApellidoRegister)
        val etEmail           = findViewById<TextInputEditText>(R.id.etEmailRegister)
        val etPassword        = findViewById<TextInputEditText>(R.id.etPasswordRegister)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPasswordRegister)
        val btnRegister       = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val nombre          = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val email           = etEmail.text.toString().trim()
            val password        = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            when {
                nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    etConfirmPassword.error = "Las contraseñas no coinciden"
                }
                password.length < 6 -> {
                    etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                }
                else -> {
                    viewModel.register(email, password, nombre, apellido)
                }
            }
        }

        viewModel.registerState.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al registrar: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}