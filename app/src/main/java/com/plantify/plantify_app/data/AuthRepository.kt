package com.plantify.plantify_app.data

import com.google.firebase.auth.FirebaseAuth
import com.plantify.plantify_app.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            Result.success(Usuario(uid = firebaseUser.uid, email = firebaseUser.email ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            Result.success(Usuario(uid = firebaseUser.uid, email = firebaseUser.email ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): Usuario? {
        val firebaseUser = auth.currentUser ?: return null
        return Usuario(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
    }
}