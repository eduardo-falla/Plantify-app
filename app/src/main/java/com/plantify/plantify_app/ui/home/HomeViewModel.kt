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

class HomeViewModel(
    private val plantRepository: PlantRepository = PlantRepository(),
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {

    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> = _plants

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadPlants() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val result = plantRepository.getPlants()
            result.onSuccess { _plants.postValue(it) }
            _isLoading.postValue(false)
        }
    }

    fun addToCart(plant: Plant) {
        viewModelScope.launch {
            val item = CartItem(
                plantId = plant.plantaId,
                nombre = plant.nombre,
                precio = plant.precio,
                imagenUrl = plant.imagenUrl
            )
            cartRepository.addItem(item)
        }
    }

    fun logout() {
        // Si tienes AuthRepository puedes llamarlo aquí
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
    }
}