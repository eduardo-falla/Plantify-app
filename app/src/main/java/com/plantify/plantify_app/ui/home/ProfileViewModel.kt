package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.AuthRepository
import com.plantify.plantify_app.model.Usuario
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _cuentaEliminada = MutableLiveData<Boolean>()
    val cuentaEliminada: LiveData<Boolean> = _cuentaEliminada

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUserFull()
            user?.let { _usuario.value = it }
            _isLoading.value = false
        }
    }

    fun eliminarCuenta() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = authRepository.getCurrentUser()?.uid ?: run {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
                return@launch
            }
            authRepository.eliminarCuenta(uid)
                .onSuccess {
                    _cuentaEliminada.value = true
                }
                .onFailure {
                    _error.value = "Error al eliminar cuenta: ${it.message}"
                }
            _isLoading.value = false
        }
    }
}