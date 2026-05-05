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

    // Crear pedido
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
                orderId    = ref.id,
                userId     = uid,
                items      = items,
                total      = total,
                estado     = "pendiente",
                direccion  = direccion,
                metodoPago = "QR"
            )
            ref.set(order).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener pedidos del usuario actual
    suspend fun getMyOrders(): Result<List<Order>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = ordersRef
                .whereEqualTo("userId", uid)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get().await()

            val orders = snapshot.documents.mapNotNull {
                it.toObject(Order::class.java)?.copy(orderId = it.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener detalle de un pedido
    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc   = ordersRef.document(orderId).get().await()
            val order = doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                ?: return Result.failure(Exception("Pedido no encontrado"))
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}