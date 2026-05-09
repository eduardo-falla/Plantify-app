package com.plantify.plantify_app.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.plantify.plantify_app.data.CartRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CheckoutViewModel(
    private val cartRepository: CartRepository = CartRepository()
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

    fun loadQrPago() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("pagos")
                    .limit(1)
                    .get().await()
                val url = snapshot.documents.firstOrNull()
                    ?.getString("qrUrl") ?: ""
                _qrUrl.value = url
            } catch (e: Exception) {
                Log.e("Checkout", "Error cargando QR: ${e.message}")
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
                // Paso 1 — verificar usuario
                Log.d("Checkout", "Paso 1 - verificando usuario")
                val uid = auth.currentUser?.uid ?: run {
                    Log.e("Checkout", "Usuario no autenticado")
                    _error.value = "Usuario no autenticado"
                    _isLoading.value = false
                    return@launch
                }
                Log.d("Checkout", "Paso 2 - uid: $uid")

                // Paso 2 — obtener carrito
                val cartResult = cartRepository.getCartItems()
                Log.d("Checkout", "Paso 3 - carrito obtenido: ${cartResult.isSuccess}")

                if (cartResult.isFailure) {
                    Log.e("Checkout", "Error carrito: ${cartResult.exceptionOrNull()?.message}")
                    _error.value = "Error al obtener el carrito"
                    _isLoading.value = false
                    return@launch
                }

                val items = cartResult.getOrNull() ?: emptyList()
                Log.d("Checkout", "Paso 4 - items en carrito: ${items.size}")

                if (items.isEmpty()) {
                    Log.e("Checkout", "Carrito vacío")
                    _error.value = "El carrito está vacío"
                    _isLoading.value = false
                    return@launch
                }

                // Paso 3 — subir comprobante
                Log.d("Checkout", "Paso 5 - subiendo comprobante a Storage")
                val storageRef = storage.reference
                    .child("comprobantes/$uid/${System.currentTimeMillis()}.jpg")

                storageRef.putFile(comprobante).await()
                Log.d("Checkout", "Paso 6 - comprobante subido exitosamente")

                val comprobanteUrl = storageRef.downloadUrl.await().toString()
                Log.d("Checkout", "Paso 7 - URL comprobante: $comprobanteUrl")

                // Paso 4 — crear pedido en Firestore
                val pedidoRef = db.collection("pedidos").document()
                Log.d("Checkout", "Paso 8 - pedidoRef id: ${pedidoRef.id}")

                val itemsMaps = items.map { item ->
                    hashMapOf(
                        "plantId"   to item.plantId,
                        "nombre"    to item.nombre,
                        "precio"    to item.precio,
                        "imagenUrl" to item.imagenUrl,
                        "quantity"  to item.quantity
                    )
                }
                Log.d("Checkout", "Paso 9 - items mapeados: ${itemsMaps.size}")

                val pedidoData = hashMapOf<String, Any>(
                    "pedidoId"       to pedidoRef.id,
                    "compradorId"    to uid,
                    "items"          to itemsMaps,
                    "total"          to total,
                    "estado"         to "pendiente",
                    "direccion"      to direccion,
                    "metodoPago"     to "QR",
                    "comprobanteUrl" to comprobanteUrl,
                    "fecha"          to FieldValue.serverTimestamp()
                )

                Log.d("Checkout", "Paso 10 - guardando pedido en Firestore")
                pedidoRef.set(pedidoData).await()
                Log.d("Checkout", "Paso 11 - pedido guardado exitosamente")

                // Paso 5 — limpiar carrito
                Log.d("Checkout", "Paso 12 - limpiando carrito")
                cartRepository.clearCart()
                Log.d("Checkout", "Paso 13 - carrito limpiado")

                _success.value = true
                Log.d("Checkout", "Paso 14 - ÉXITO TOTAL")

            } catch (e: Exception) {
                Log.e("Checkout", "ERROR en confirmarPago: ${e.message}", e)
                _error.value = "Error: ${e.message}"
            }
            _isLoading.value = false
        }
    }
}