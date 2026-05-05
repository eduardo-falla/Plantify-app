package com.plantify.plantify_app.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.plantify.plantify_app.data.CartRepository
import com.plantify.plantify_app.data.OrderRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CheckoutViewModel(
    private val cartRepository: CartRepository = CartRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth    = FirebaseAuth.getInstance()

    private val _qrUrl = MutableLiveData<String>()
    val qrUrl: LiveData<String> = _qrUrl

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Carga el QR de pago guardado en Firestore
    fun loadQrPago() {
        viewModelScope.launch {
            try {
                // Busca en la colección "pagos" el primer documento
                val snapshot = db.collection("pagos")
                    .limit(1)
                    .get().await()

                val url = snapshot.documents.firstOrNull()
                    ?.getString("qrUrl") ?: ""

                _qrUrl.value = url
            } catch (e: Exception) {
                _error.value = "No se pudo cargar el QR de pago"
            }
        }
    }
    fun confirmarPago(
        direccion: String,
        comprobante: Uri,
        total: Double,
        subtotal: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Obtener items del carrito
                val cartResult = cartRepository.getCartItems()
                if (cartResult.isFailure) {
                    _error.value = "Error al obtener el carrito"
                    _isLoading.value = false
                    return@launch
                }
                val items = cartResult.getOrNull() ?: emptyList()

                // 2. Subir comprobante a Storage
                val uid = auth.currentUser?.uid ?: run {
                    _error.value = "Usuario no autenticado"
                    _isLoading.value = false
                    return@launch
                }
                val ref = storage.reference
                    .child("comprobantes/$uid/${System.currentTimeMillis()}.jpg")
                ref.putFile(comprobante).await()
                val comprobanteUrl = ref.downloadUrl.await().toString()

                // 3. Crear pedido en Firestore
                val orderResult = orderRepository.createOrder(
                    items     = items,
                    total     = total,
                    direccion = direccion
                )

                if (orderResult.isSuccess) {
                    val orderId = orderResult.getOrNull() ?: ""

                    // 4. Guardar URL del comprobante en el pedido
                    db.collection("pedidos")
                        .document(orderId)
                        .update("comprobanteUrl", comprobanteUrl)
                        .await()

                    // 5. Limpiar carrito
                    cartRepository.clearCart()

                    _success.value = true
                } else {
                    _error.value = "Error al crear el pedido"
                }

            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
            _isLoading.value = false
        }
    }
}