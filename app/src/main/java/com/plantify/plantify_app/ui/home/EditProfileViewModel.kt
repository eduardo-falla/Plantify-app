package com.plantify.plantify_app.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.AuthRepository
import com.plantify.plantify_app.model.Usuario
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _saved = MutableLiveData<Boolean>()
    val saved: LiveData<Boolean> = _saved

    fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = repository.getCurrentUserFull()
            user?.let { _usuario.value = it }
            _isLoading.value = false
        }
    }

    fun saveChanges(nombre: String) {
        val uid = repository.getCurrentUser()?.uid ?: return
        val fotoUrl = _usuario.value?.fotoPerfil ?: ""

        if (nombre.trim().isEmpty()) {
            _message.value = "El nombre no puede estar vacío"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            repository.updateUser(uid, nombre.trim(), fotoUrl)
                .onSuccess {
                    _message.value = "Perfil actualizado ✅"
                    _saved.value = true
                }
                .onFailure {
                    _message.value = "Error al guardar: ${it.message}"
                }
            _isLoading.value = false
        }
    }

    fun uploadPhoto(imageUri: Uri) {
        val uid = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.uploadProfilePhoto(uid, imageUri)
                .onSuccess { url ->
                    _usuario.value = _usuario.value?.copy(fotoPerfil = url)
                    _message.value = "Foto actualizada ✅"
                }
                .onFailure {
                    _message.value = "Error al subir foto: ${it.message}"
                }
            _isLoading.value = false
        }
    }

    fun clearMessage() { _message.value = null }
}