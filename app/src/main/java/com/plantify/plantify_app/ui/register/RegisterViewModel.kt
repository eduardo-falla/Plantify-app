package com.plantify.plantify_app.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.AuthRepository
import com.plantify.plantify_app.model.User
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _registerState = MutableLiveData<Result<User>>()
    val registerState: LiveData<Result<User>> = _registerState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.register(email, password)
            _registerState.value = result
        }
    }
}