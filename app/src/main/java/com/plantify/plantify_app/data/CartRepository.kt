package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.CartItem
import kotlinx.coroutines.tasks.await

class CartRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun cartCollection() =
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("cart")

    suspend fun getCartItems(): Result<List<CartItem>> {
        return try {
            val snapshot = cartCollection().get().await()
            val items = snapshot.documents.mapNotNull {
                it.toObject(CartItem::class.java)?.copy(id = it.id)
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addItem(item: CartItem): Result<Unit> {
        return try {
            // Si ya existe la planta, incrementa cantidad
            val existing = cartCollection()
                .whereEqualTo("plantId", item.plantId)
                .get().await()

            if (existing.isEmpty) {
                cartCollection().add(item).await()
            } else {
                val doc = existing.documents.first()
                val currentQty = doc.getLong("quantity")?.toInt() ?: 1
                cartCollection().document(doc.id)
                    .update("quantity", currentQty + 1).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeItem(itemId: String): Result<Unit> {
        return try {
            cartCollection().document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(itemId: String, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                removeItem(itemId)
            } else {
                cartCollection().document(itemId)
                    .update("quantity", quantity).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            val items = cartCollection().get().await()
            items.documents.forEach { it.reference.delete().await() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}