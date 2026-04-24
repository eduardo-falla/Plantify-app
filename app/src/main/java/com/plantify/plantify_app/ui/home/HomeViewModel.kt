package com.plantify.plantify_app.ui.home

import androidx.lifecycle.ViewModel
import com.plantify.plantify_app.data.AuthRepository

class HomeViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun logout() {
        repository.logout()
    }

    fun getCurrentUser() = repository.getCurrentUser()
}