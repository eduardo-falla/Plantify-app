package com.plantify.plantify_app.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.AuthRepository
import com.plantify.plantify_app.model.Usuario
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _registerState = MutableLiveData<Result<Usuario>>()
    val registerState: LiveData<Result<Usuario>> = _registerState

    // 🆕 nombre como parámetro
    fun register(email: String, password: String, nombre: String, apellido: String) {
        viewModelScope.launch {
            _registerState.value = repository.register(email, password, nombre, apellido)
        }
    }
}