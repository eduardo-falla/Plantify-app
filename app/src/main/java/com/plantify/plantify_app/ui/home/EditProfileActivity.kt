package com.plantify.plantify_app.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.plantify.plantify_app.R

class EditProfileActivity : AppCompatActivity() {

    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var ivPhoto: ImageView
    private lateinit var etNombre: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var btnChangePhoto: Button

    // Launcher para seleccionar imagen de galería
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Muestra preview inmediato
            Glide.with(this).load(it).circleCrop().into(ivPhoto)
            // Sube a Firebase Storage
            viewModel.uploadPhoto(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ivPhoto       = findViewById(R.id.ivEditPhoto)
        etNombre      = findViewById(R.id.etEditNombre)
        btnSave       = findViewById(R.id.btnSaveProfile)
        btnBack       = findViewById(R.id.btnBackEdit)
        progressBar   = findViewById(R.id.progressBarEdit)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)

        // Volver
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Cambiar foto
        btnChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Guardar cambios
        btnSave.setOnClickListener {
            val nombre = etNombre.text.toString()
            viewModel.saveChanges(nombre)
        }

        // Observers
        viewModel.usuario.observe(this) { usuario ->
            etNombre.setText(usuario.nombre)

            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditEmail)
                .setText(usuario.email)

            if (usuario.fotoPerfil.isNotEmpty()) {
                Glide.with(this)
                    .load(usuario.fotoPerfil)
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivPhoto)
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnSave.isEnabled      = !loading
            btnChangePhoto.isEnabled = !loading
        }

        viewModel.message.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        viewModel.saved.observe(this) { saved ->
            if (saved == true) finish()
        }

        viewModel.loadUser()
    }
}