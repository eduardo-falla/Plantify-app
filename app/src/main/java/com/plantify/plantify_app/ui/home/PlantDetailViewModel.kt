package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.CartRepository
import com.plantify.plantify_app.data.PlantRepository
import com.plantify.plantify_app.model.CartItem
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val plantRepository: PlantRepository = PlantRepository(),
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {

    private val _plant = MutableLiveData<Plant>()
    val plant: LiveData<Plant> = _plant

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cartMessage = MutableLiveData<String?>()
    val cartMessage: LiveData<String?> = _cartMessage

    fun loadPlant(plantaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            plantRepository.getPlantById(plantaId)
                .onSuccess { plant ->
                    _plant.value = plant
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Error al cargar la planta"
                }
            _isLoading.value = false
        }
    }

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            cartRepository.addItem(item)
                .onSuccess {
                    _cartMessage.value = "${item.nombre} agregado al carrito 🌿"
                }
                .onFailure {
                    _cartMessage.value = "Error al agregar al carrito"
                }
        }
    }

    fun clearCartMessage() { _cartMessage.value = null }
}