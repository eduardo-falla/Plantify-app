package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.FavoritosRepository
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val repository: FavoritosRepository = FavoritosRepository()
) : ViewModel() {

    private val _favoritos = MutableLiveData<List<Plant>>()
    val favoritos: LiveData<List<Plant>> = _favoritos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun loadFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getFavoritos()
                .onSuccess { _favoritos.value = it }
                .onFailure { _message.value = "Error al cargar favoritos" }
            _isLoading.value = false
        }
    }

    fun quitarFavorito(plantaId: String) {
        viewModelScope.launch {
            repository.quitarFavorito(plantaId)
                .onSuccess {
                    _message.value = "Eliminado de favoritos"
                    loadFavoritos()
                }
                .onFailure {
                    _message.value = "Error al eliminar favorito"
                }
        }
    }

    fun clearMessage() { _message.value = null }
}