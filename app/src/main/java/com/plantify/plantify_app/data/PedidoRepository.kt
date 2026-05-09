package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Pedido
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PedidoRepository {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun pedidosCollection() =
        db.collection("pedidos")
            .document(auth.currentUser?.uid ?: "guest")
            .collection("mis_pedidos")

    suspend fun guardarPedido(pedido: Pedido): Result<Unit> {
        return try {
            pedidosCollection().add(pedido).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPedidosFlow(): Flow<List<Pedido>> = callbackFlow {
        val listener = pedidosCollection()
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pedidos = snapshot?.documents?.mapNotNull {
                    it.toObject(Pedido::class.java)?.copy(pedidoId = it.id)
                } ?: emptyList()
                trySend(pedidos)
            }
        awaitClose { listener.remove() }
    }
}