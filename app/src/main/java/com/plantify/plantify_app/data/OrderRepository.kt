package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.plantify.plantify_app.model.CartItem
import com.plantify.plantify_app.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepository {

    private val db        = FirebaseFirestore.getInstance()
    private val auth      = FirebaseAuth.getInstance()
    private val ordersRef = db.collection("pedidos")

    suspend fun createOrder(
        items: List<CartItem>,
        total: Double,
        direccion: String
    ): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val ref   = ordersRef.document()
            val order = Order(
                pedidoId    = ref.id,
                compradorId = uid,
                items       = items,
                total       = total,
                estado      = "pendiente",
                direccion   = direccion,
                metodoPago  = "QR"
            )
            ref.set(order).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyOrders(): Result<List<Order>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = ordersRef
                .whereEqualTo("compradorId", uid)
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

                    Order(
                        pedidoId       = doc.id,
                        compradorId    = doc.getString("compradorId") ?: "",
                        items          = items,
                        total          = doc.getDouble("total") ?: 0.0,
                        estado         = doc.getString("estado") ?: "pendiente",
                        direccion      = doc.getString("direccion") ?: "",
                        metodoPago     = doc.getString("metodoPago") ?: "QR",
                        comprobanteUrl = doc.getString("comprobanteUrl") ?: "",
                        fecha          = doc.getTimestamp("fecha")?.toDate()
                    )
                } catch (e: Exception) { null }
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(pedidoId: String): Result<Order> {
        return try {
            val doc   = ordersRef.document(pedidoId).get().await()
            val order = doc.toObject(Order::class.java)?.copy(pedidoId = doc.id)
                ?: return Result.failure(Exception("Pedido no encontrado"))
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}