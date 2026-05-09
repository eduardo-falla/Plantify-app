package com.plantify.plantify_app.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.plantify.plantify_app.R
import com.plantify.plantify_app.data.CartRepository
import com.plantify.plantify_app.data.PedidoRepository
import com.plantify.plantify_app.model.CartItem
import com.plantify.plantify_app.model.Pedido
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class PagoViewModel(
    private val cartRepository: CartRepository = CartRepository(),
    private val pedidoRepository: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _pagoExitoso = MutableLiveData<Boolean>()
    val pagoExitoso: LiveData<Boolean> = _pagoExitoso

    private val _linkPago = MutableLiveData<String>()
    val linkPago: LiveData<String> = _linkPago

    private val ACCESS_TOKEN = "token de pago"

    fun generarLinkPago(total: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val json = JSONObject().apply {
                    put("title", "Compra Plantify")
                    put("quantity", 1)
                    put("unit_price", total)
                    put("currency_id", "PEN")
                }
                val body = JSONObject().apply {
                    put("items", org.json.JSONArray().put(json))
                }
                val request = Request.Builder()
                    .url("https://api.mercadopago.com/checkout/preferences")
                    .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                    .addHeader("Content-Type", "application/json")
                    .post(body.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "")
                val link = jsonResponse.getString("init_point")
                _linkPago.postValue(link)
            } catch (e: Exception) {
                _linkPago.postValue("")
            }
        }
    }

    fun confirmarPago(items: List<CartItem>, total: Double) {
        viewModelScope.launch {
            val pedido = Pedido(
                usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                items = items,
                total = total,
                estado = "pagado"
            )
            val result = pedidoRepository.guardarPedido(pedido)
            result.onSuccess {
                cartRepository.clearCart()
                _pagoExitoso.postValue(true)
            }
            result.onFailure {
                _pagoExitoso.postValue(false)
            }
        }
    }
}

class PagoFragment : Fragment(R.layout.fragment_pago) {

    private val viewModel: PagoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivQR         = view.findViewById<ImageView>(R.id.ivQR)
        val tvTotalPago  = view.findViewById<TextView>(R.id.tvTotalPago)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarPago)
        val btnCancelar  = view.findViewById<Button>(R.id.btnCancelarPago)

        val total = arguments?.getDouble("total") ?: 0.0
        @Suppress("UNCHECKED_CAST")
        val items = arguments?.getSerializable("items") as? ArrayList<CartItem> ?: arrayListOf()

        tvTotalPago.text = "S/ ${"%.2f".format(total)}"

        // Generar link de pago con Mercado Pago
        viewModel.generarLinkPago(total)

        viewModel.linkPago.observe(viewLifecycleOwner) { link ->
            if (link.isNotEmpty()) {
                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                        link, BarcodeFormat.QR_CODE, 600, 600
                    )
                    ivQR.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al generar QR", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Error al conectar con Mercado Pago", Toast.LENGTH_SHORT).show()
            }
        }

        btnConfirmar.setOnClickListener {
            viewModel.confirmarPago(items, total)
        }

        btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewModel.pagoExitoso.observe(viewLifecycleOwner) { exitoso ->
            if (exitoso) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, VoucherFragment.newInstance(total, items))
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Error al registrar pago", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(total: Double, items: List<CartItem>) = PagoFragment().apply {
            arguments = Bundle().apply {
                putDouble("total", total)
                putSerializable("items", ArrayList(items))
            }
        }
    }
}