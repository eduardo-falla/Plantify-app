package com.plantify.plantify_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.model.Usuario

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usuariosRef = db.collection("usuarios")

    // Operación: Crear o actualizar usuario (PK = uid)
    fun guardarUsuario(usuario: Usuario, onComplete: (Boolean) -> Unit) {
        usuariosRef.document(usuario.uid).set(usuario)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Operación: Obtener datos de un usuario por su ID
    fun obtenerUsuario(uid: String, onResult: (Usuario?) -> Unit) {
        usuariosRef.document(uid).get()
            .addOnSuccessListener { doc ->
                onResult(doc.toObject(Usuario::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }
}