package com.plantify.plantify_app.ui.admin

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

class AddEditPlantActivity : AppCompatActivity() {

    private val viewModel: AddEditPlantViewModel by viewModels()
    private var imageUri: Uri? = null
    private var esEdicion = false
    private var plantaId  = ""

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            findViewById<ImageView>(R.id.ivPlantPreview).also { iv ->
                Glide.with(this).load(it).centerCrop().into(iv)
                iv.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_plant)

        esEdicion = intent.getBooleanExtra("esEdicion", false)
        plantaId  = intent.getStringExtra("plantaId") ?: ""

        val btnBack       = findViewById<ImageButton>(R.id.btnBackAddEdit)
        val tvTitle       = findViewById<android.widget.TextView>(R.id.tvAddEditTitle)
        val etNombre      = findViewById<TextInputEditText>(R.id.etPlantNombre)
        val etDescripcion = findViewById<TextInputEditText>(R.id.etPlantDescripcion)
        val etCuidados    = findViewById<TextInputEditText>(R.id.etPlantCuidados)
        val etPrecio      = findViewById<TextInputEditText>(R.id.etPlantPrecio)
        val etStock       = findViewById<TextInputEditText>(R.id.etPlantStock)
        val etCategoria   = findViewById<TextInputEditText>(R.id.etPlantCategoria)
        val btnImagen     = findViewById<Button>(R.id.btnSeleccionarImagen)
        val btnGuardar    = findViewById<Button>(R.id.btnGuardarPlanta)
        val progressBar   = findViewById<ProgressBar>(R.id.progressBarAddEdit)
        val ivPreview     = findViewById<ImageView>(R.id.ivPlantPreview)

        tvTitle.text = if (esEdicion) "Editar planta" else "Nueva planta"

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Si es edición, cargar datos existentes
        if (esEdicion && plantaId.isNotEmpty()) {
            viewModel.loadPlant(plantaId)
        }

        btnImagen.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            val nombre      = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val cuidados    = etCuidados.text.toString().trim()
            val precio      = etPrecio.text.toString().toDoubleOrNull()
            val stock       = etStock.text.toString().toIntOrNull()
            val categoria   = etCategoria.text.toString().trim()

            when {
                nombre.isEmpty()    -> Toast.makeText(this, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
                precio == null      -> Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show()
                stock == null       -> Toast.makeText(this, "Ingresa el stock", Toast.LENGTH_SHORT).show()
                categoria.isEmpty() -> Toast.makeText(this, "Ingresa la categoría", Toast.LENGTH_SHORT).show()
                else -> {
                    viewModel.savePlant(
                        plantaId    = plantaId,
                        nombre      = nombre,
                        descripcion = descripcion,
                        cuidados    = cuidados,
                        precio      = precio,
                        stock       = stock,
                        categoria   = categoria,
                        imageUri    = imageUri,
                        esEdicion   = esEdicion
                    )
                }
            }
        }

        // Observers
        viewModel.plant.observe(this) { plant ->
            etNombre.setText(plant.nombre)
            etDescripcion.setText(plant.descripcion)
            etCuidados.setText(plant.cuidados)
            etPrecio.setText(plant.precio.toString())
            etStock.setText(plant.stock.toString())
            etCategoria.setText(plant.categoria)
            if (plant.imagenUrl.isNotEmpty()) {
                Glide.with(this).load(plant.imagenUrl).centerCrop().into(ivPreview)
                ivPreview.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnGuardar.isEnabled   = !loading
        }

        viewModel.success.observe(this) { success ->
            if (success) {
                Toast.makeText(
                    this,
                    if (esEdicion) "Planta actualizada ✅" else "Planta agregada ✅",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}