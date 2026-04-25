package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Favorito

class FavoritoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val favoritosRef = db.collection("favoritos")

    fun agregarAFavoritos(favorito: Favorito, onComplete: (Boolean) -> Unit) {
        val id = favoritosRef.document().id
        favoritosRef.document(id).set(favorito.copy(favoritoId = id))
            .addOnSuccessListener { onComplete(true) }
    }

    fun quitarDeFavoritos(favoritoId: String, onComplete: (Boolean) -> Unit) {
        favoritosRef.document(favoritoId).delete()
            .addOnSuccessListener { onComplete(true) }
    }

    fun obtenerFavoritosUsuario(usuarioId: String, onResult: (List<Favorito>) -> Unit) {
        favoritosRef.whereEqualTo("usuarioId", usuarioId).get()
            .addOnSuccessListener { docs ->
                onResult(docs.toObjects(Favorito::class.java))
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}