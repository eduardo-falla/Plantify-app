package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.CartItem

class PlantDetailActivity : AppCompatActivity() {

    private val viewModel: PlantDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        val plantaId = intent.getStringExtra("plantaId") ?: run {
            finish()
            return
        }

        // ── Vistas ────────────────────────────────────────────
        val ivPlant       = findViewById<ImageView>(R.id.ivPlantDetail)
        val tvNombre      = findViewById<TextView>(R.id.tvNombreDetail)
        val tvCategoria   = findViewById<TextView>(R.id.tvCategoriaDetail)
        val tvPrecio      = findViewById<TextView>(R.id.tvPrecioDetail)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcionDetail)
        val tvCuidados    = findViewById<TextView>(R.id.tvCuidadosDetail)
        val tvStock       = findViewById<TextView>(R.id.tvStockDetail)
        val btnAgregar    = findViewById<Button>(R.id.btnAgregarCarrito)
        val btnBack       = findViewById<ImageButton>(R.id.btnBack)
        val progressBar   = findViewById<ProgressBar>(R.id.progressBar)

        // ── Botón volver — va aquí, FUERA del observer ────────
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ── Observers ─────────────────────────────────────────
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.cartMessage.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearCartMessage()
            }
        }

        viewModel.plant.observe(this) { plant ->

            // Llenar vistas con datos
            tvNombre.text      = plant.nombre
            tvCategoria.text   = plant.categoria
            tvPrecio.text      = "S/ ${"%.2f".format(plant.precio)}"
            tvDescripcion.text = plant.descripcion
            tvCuidados.text    = plant.cuidados
            tvStock.text       = if (plant.stock > 0)
                "✅ Stock disponible: ${plant.stock}"
            else
                "❌ Sin stock"

            // Habilitar o deshabilitar botón según stock
            btnAgregar.isEnabled = plant.stock > 0
            btnAgregar.alpha     = if (plant.stock > 0) 1f else 0.5f

            // Imagen
            Glide.with(this)
                .load(plant.imagenUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(ivPlant)

            // ── Botón agregar — va DENTRO del observer
            //    para tener acceso al objeto plant ya cargado ──
            btnAgregar.setOnClickListener {
                if (plant.stock > 0) {
                    viewModel.addToCart(
                        CartItem(
                            plantId   = plant.plantaId,
                            nombre    = plant.nombre,
                            precio    = plant.precio,
                            imagenUrl = plant.imagenUrl
                        )
                    )
                } else {
                    Toast.makeText(this, "Esta planta no tiene stock", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ── Cargar datos ──────────────────────────────────────
        viewModel.loadPlant(plantaId)
    }
}