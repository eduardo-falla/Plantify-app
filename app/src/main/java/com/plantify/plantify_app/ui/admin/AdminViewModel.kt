package com.plantify.plantify_app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.plantify.plantify_app.data.PlantRepository
import com.plantify.plantify_app.model.CartItem
import com.plantify.plantify_app.model.Order
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel(
    private val plantRepository: PlantRepository = PlantRepository()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _plantas = MutableLiveData<List<Plant>>()
    val plantas: LiveData<List<Plant>> = _plantas

    private val _pedidos = MutableLiveData<List<Order>>()
    val pedidos: LiveData<List<Order>> = _pedidos

    private val _totalPlantas = MutableLiveData<Int>()
    val totalPlantas: LiveData<Int> = _totalPlantas

    private val _totalPedidos = MutableLiveData<Int>()
    val totalPedidos: LiveData<Int> = _totalPedidos

    private val _totalUsuarios = MutableLiveData<Int>()
    val totalUsuarios: LiveData<Int> = _totalUsuarios

    private val _pedidosPendientes = MutableLiveData<Int>()
    val pedidosPendientes: LiveData<Int> = _pedidosPendientes

    private val _pedidosEntregados = MutableLiveData<Int>()
    val pedidosEntregados: LiveData<Int> = _pedidosEntregados

    private val _pedidosCancelados = MutableLiveData<Int>()
    val pedidosCancelados: LiveData<Int> = _pedidosCancelados

    private val _totalIngresos = MutableLiveData<Double>()
    val totalIngresos: LiveData<Double> = _totalIngresos

    private val _plantaMasVendida = MutableLiveData<String>()
    val plantaMasVendida: LiveData<String> = _plantaMasVendida

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    // ── Dashboard ─────────────────────────────────────────────
    fun loadDashboard() {
        viewModelScope.launch {
            try {
                val plantas = db.collection("plantas").get().await()
                _totalPlantas.value = plantas.size()

                val pedidos = db.collection("pedidos").get().await()
                _totalPedidos.value = pedidos.size()

                val usuarios = db.collection("usuarios").get().await()
                _totalUsuarios.value = usuarios.size()

                var pendientes   = 0
                var entregados   = 0
                var cancelados   = 0
                var ingresos     = 0.0
                val conteoPlanta = mutableMapOf<String, Int>()

                pedidos.documents.forEach { doc ->
                    when (doc.getString("estado")) {
                        "pendiente"  -> pendientes++
                        "entregado"  -> entregados++
                        "cancelado"  -> cancelados++
                    }

                    val estado = doc.getString("estado")
                    if (estado == "entregado" || estado == "confirmado" || estado == "enviado") {
                        ingresos += doc.getDouble("total") ?: 0.0
                    }

                    val items = doc.get("items") as? List<*>
                    items?.forEach { item ->
                        val map    = item as? Map<*, *>
                        val nombre = map?.get("nombre") as? String ?: ""
                        val qty    = (map?.get("quantity") as? Long)?.toInt() ?: 1
                        if (nombre.isNotEmpty()) {
                            conteoPlanta[nombre] = (conteoPlanta[nombre] ?: 0) + qty
                        }
                    }
                }

                _pedidosPendientes.value = pendientes
                _pedidosEntregados.value = entregados
                _pedidosCancelados.value = cancelados
                _totalIngresos.value     = ingresos
                _plantaMasVendida.value  = conteoPlanta
                    .maxByOrNull { it.value }?.key ?: "Sin datos"

            } catch (e: Exception) {
                _message.value = "Error al cargar dashboard"
            }
        }
    }

    // ── Plantas ───────────────────────────────────────────────
    fun loadPlantas() {
        viewModelScope.launch {
            _isLoading.value = true
            plantRepository.getPlants()
                .onSuccess { _plantas.value = it }
                .onFailure { _message.value = "Error al cargar plantas" }
            _isLoading.value = false
        }
    }

    fun deletePlant(plantaId: String) {
        viewModelScope.launch {
            try {
                db.collection("plantas").document(plantaId).delete().await()
                _message.value = "Planta eliminada ✅"
                loadPlantas()
                loadDashboard()
            } catch (e: Exception) {
                _message.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    // ── Pedidos ───────────────────────────────────────────────
    fun loadPedidos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("pedidos")
                    .orderBy("fecha", Query.Direction.DESCENDING)
                    .get().await()

                val orders = snapshot.documents.mapNotNull { doc ->
                    try {
                        val itemsList = doc.get("items") as? List<*>
                        val items = itemsList?.mapNotNull { itemRaw ->
                            val map = itemRaw as? Map<*, *>
                                ?: return@mapNotNull null

                            val precio = when (val p = map["precio"]) {
                                is Double -> p
                                is Long   -> p.toDouble()
                                else      -> 0.0
                            }

                            CartItem(
                                plantId   = map["plantId"]   as? String ?: "",
                                nombre    = map["nombre"]    as? String ?: "",
                                precio    = precio,
                                imagenUrl = map["imagenUrl"] as? String ?: "",
                                quantity  = (map["quantity"] as? Long)?.toInt() ?: 1
                            )
                        } ?: emptyList()

                        // Buscar nombre y email del comprador
                        val compradorId = doc.getString("compradorId") ?: ""
                        var compradorNombre = "Sin nombre"
                        var compradorEmail  = "Sin email"

                        if (compradorId.isNotEmpty()) {
                            try {
                                val usuarioDoc = db.collection("usuarios")
                                    .document(compradorId)
                                    .get().await()
                                compradorNombre = usuarioDoc.getString("nombre") ?: "Sin nombre"
                                compradorEmail  = usuarioDoc.getString("email")  ?: "Sin email"
                            } catch (e: Exception) {
                                // Si no encuentra el usuario continúa igual
                            }
                        }

                        Order(
                            pedidoId        = doc.id,
                            compradorId     = compradorId,
                            compradorNombre = compradorNombre,
                            compradorEmail  = compradorEmail,
                            items           = items,
                            total           = doc.getDouble("total") ?: 0.0,
                            estado          = doc.getString("estado") ?: "pendiente",
                            direccion       = doc.getString("direccion") ?: "",
                            metodoPago      = doc.getString("metodoPago") ?: "QR",
                            comprobanteUrl  = doc.getString("comprobanteUrl") ?: "",
                            fecha           = doc.getTimestamp("fecha")?.toDate()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _pedidos.value = orders

            } catch (e: Exception) {
                _message.value = "Error al cargar pedidos"
            }
            _isLoading.value = false
        }
    }

    fun updateOrderStatus(pedidoId: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                db.collection("pedidos")
                    .document(pedidoId)
                    .update("estado", nuevoEstado).await()
                _message.value = "Estado actualizado ✅"
                loadPedidos()
                loadDashboard()
            } catch (e: Exception) {
                _message.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun clearMessage() { _message.value = null }
}