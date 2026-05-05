package com.plantify.plantify_app.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.plantify.plantify_app.R

class CheckoutActivity : AppCompatActivity() {

    private val viewModel: CheckoutViewModel by viewModels()
    private var comprobante: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            comprobante = it
            val ivComprobante = findViewById<ImageView>(R.id.ivComprobante)
            Glide.with(this).load(it).centerCrop().into(ivComprobante)
            ivComprobante.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvComprobanteStatus).text = "✅ Comprobante adjuntado"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val subtotal    = intent.getDoubleExtra("subtotal", 0.0)
        val total       = intent.getDoubleExtra("total", 0.0)

        val btnBack          = findViewById<ImageButton>(R.id.btnBackCheckout)
        val tvSubtotal       = findViewById<TextView>(R.id.tvCheckoutSubtotal)
        val tvShipping       = findViewById<TextView>(R.id.tvCheckoutShipping)
        val tvTotal          = findViewById<TextView>(R.id.tvCheckoutTotal)
        val etDireccion      = findViewById<TextInputEditText>(R.id.etDireccion)
        val btnAdjuntar      = findViewById<Button>(R.id.btnAdjuntarComprobante)
        val btnPagar         = findViewById<Button>(R.id.btnConfirmarPago)
        val progressBar      = findViewById<ProgressBar>(R.id.progressBarCheckout)
        val ivQr             = findViewById<ImageView>(R.id.ivQrPago)
        val tvComprobanteStatus = findViewById<TextView>(R.id.tvComprobanteStatus)

        // Mostrar resumen
        tvSubtotal.text = "S/ ${"%.2f".format(subtotal)}"
        tvShipping.text = "S/ 5.99"
        tvTotal.text    = "S/ ${"%.2f".format(total)}"

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Cargar QR de pago de la empresa
        viewModel.loadQrPago()

        // Botón adjuntar comprobante
        btnAdjuntar.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Botón confirmar pago
        btnPagar.setOnClickListener {
            val direccion = etDireccion.text.toString().trim()

            when {
                direccion.isEmpty() -> {
                    Toast.makeText(this, "Ingresa tu dirección de entrega", Toast.LENGTH_SHORT).show()
                }
                comprobante == null -> {
                    Toast.makeText(this, "Adjunta el comprobante de pago", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.confirmarPago(
                        direccion    = direccion,
                        comprobante  = comprobante!!,
                        total        = total,
                        subtotal     = subtotal
                    )
                }
            }
        }

        // Observers
        viewModel.qrUrl.observe(this) { url ->
            if (url.isNotEmpty()) {
                Glide.with(this).load(url).into(ivQr)
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnPagar.isEnabled     = !loading
            btnAdjuntar.isEnabled  = !loading
        }

        viewModel.success.observe(this) { success ->
            if (success) {
                Toast.makeText(
                    this,
                    "¡Pedido confirmado! Te contactaremos pronto 🌿",
                    Toast.LENGTH_LONG
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