package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.plantify.plantify_app.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth        = FirebaseAuth.getInstance()
    private val db          = FirebaseFirestore.getInstance()
    private val storage     = FirebaseStorage.getInstance()
    private val usuariosRef = db.collection("usuarios")

    // ── LOGIN EMAIL ───────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val result       = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            val usuario      = obtenerUsuarioFirestore(firebaseUser.uid)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── REGISTRO ──────────────────────────────────────────────
    suspend fun register(email: String, password: String, nombre: String): Result<Usuario> {
        return try {
            val result       = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            val usuario      = Usuario(
                uid        = firebaseUser.uid,
                email      = firebaseUser.email ?: "",
                nombre     = nombre,
                fotoPerfil = "",
                rol        = "comprador"
            )
            usuariosRef.document(usuario.uid).set(usuario).await()
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── LOGIN GOOGLE ──────────────────────────────────────────
    suspend fun loginWithGoogle(idToken: String): Result<Usuario> {
        return try {
            val credential   = GoogleAuthProvider.getCredential(idToken, null)
            val result       = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!

            val doc     = usuariosRef.document(firebaseUser.uid).get().await()
            val usuario = if (doc.exists()) {
                doc.toObject(Usuario::class.java)!!
            } else {
                val nuevoUsuario = Usuario(
                    uid        = firebaseUser.uid,
                    email      = firebaseUser.email ?: "",
                    nombre     = firebaseUser.displayName ?: "",
                    fotoPerfil = firebaseUser.photoUrl?.toString() ?: "",
                    rol        = "comprador"
                )
                usuariosRef.document(nuevoUsuario.uid).set(nuevoUsuario).await()
                nuevoUsuario
            }
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── OBTENER USUARIO POR UID ───────────────────────────────
    suspend fun getUserById(uid: String): Usuario? {
        return try {
            val doc = usuariosRef.document(uid).get().await()
            doc.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // ── OBTENER USUARIO COMPLETO DESDE FIRESTORE ──────────────
    suspend fun getCurrentUserFull(): Usuario? {
        val firebaseUser = auth.currentUser ?: return null
        return getUserById(firebaseUser.uid)
    }

    // ── OBTENER USUARIO BÁSICO (solo Auth, sin Firestore) ─────
    fun getCurrentUser(): Usuario? {
        val firebaseUser = auth.currentUser ?: return null
        return Usuario(
            uid   = firebaseUser.uid,
            email = firebaseUser.email ?: ""
        )
    }

    // ── ACTUALIZAR NOMBRE Y FOTO ──────────────────────────────
    suspend fun updateUser(uid: String, nombre: String, fotoUrl: String): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "nombre"     to nombre,
                "fotoPerfil" to fotoUrl
            )
            usuariosRef.document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure<Unit>(e)
        }
    }

    // ── SUBIR FOTO DE PERFIL A STORAGE ────────────────────────
    suspend fun uploadProfilePhoto(uid: String, imageUri: android.net.Uri): Result<String> {
        return try {
            val ref = storage.reference.child("profile_photos/$uid.jpg")
            ref.putFile(imageUri).await()
            val url     = ref.downloadUrl.await().toString()
            val updates = hashMapOf<String, Any>("fotoPerfil" to url)
            usuariosRef.document(uid).update(updates).await()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure<String>(e)
        }
    }

    // ── LOGOUT ────────────────────────────────────────────────
    fun logout() {
        auth.signOut()
    }

    // ── PRIVADO: obtiene usuario de Firestore por UID ─────────
    private suspend fun obtenerUsuarioFirestore(uid: String): Usuario {
        val doc = usuariosRef.document(uid).get().await()
        return doc.toObject(Usuario::class.java) ?: Usuario(uid = uid)
    }
}