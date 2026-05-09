package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.FavoritoRepository
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.launch

class FavoritoViewModel(
    private val repository: FavoritoRepository = FavoritoRepository()
) : ViewModel() {

    private val _favoritos = MutableLiveData<List<Plant>>()
    val favoritos: LiveData<List<Plant>> = _favoritos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadFavoritos() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val result = repository.getFavoritos()
            result.onSuccess { _favoritos.postValue(it) }
            _isLoading.postValue(false)
        }
    }

    fun removeFavorito(plantaId: String) {
        viewModelScope.launch {
            repository.removeFavorito(plantaId)
            loadFavoritos()
        }
    }
}