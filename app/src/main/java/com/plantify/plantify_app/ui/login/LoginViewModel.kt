package com.plantify.plantify_app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.AuthRepository
import com.plantify.plantify_app.model.Usuario
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<Result<Usuario>>()
    val loginState: LiveData<Result<Usuario>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = repository.login(email, password)
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = repository.loginWithGoogle(idToken)
        }
    }
}