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

    private val _allPlants = MutableLiveData<List<Plant>>()

    private val _filteredPlants = MutableLiveData<List<Plant>>()
    val filteredPlants: LiveData<List<Plant>> = _filteredPlants

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cartMessage = MutableLiveData<String?>()
    val cartMessage: LiveData<String?> = _cartMessage

    fun loadPlants() {
        viewModelScope.launch {
            _isLoading.value = true
            plantRepository.getPlants()
                .onSuccess { plants ->
                    _allPlants.value = plants
                    _filteredPlants.value = plants
                    // Categorías únicas con "Todas" al inicio
                    val cats = mutableListOf("Todas")
                    cats.addAll(plants.map { it.categoria }.distinct())
                    _categories.value = cats
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Error al cargar plantas"
                }
            _isLoading.value = false
        }
    }

    fun filterByCategory(categoria: String) {
        val all = _allPlants.value ?: return
        _filteredPlants.value = if (categoria == "Todas") all
        else all.filter { it.categoria == categoria }
    }

    fun searchPlants(query: String) {
        val all = _allPlants.value ?: return
        _filteredPlants.value = if (query.isEmpty()) all
        else all.filter {
            it.nombre.contains(query, ignoreCase = true) ||
                    it.categoria.contains(query, ignoreCase = true)
        }
    }

    fun addToCart(plant: Plant) {
        viewModelScope.launch {
            val item = CartItem(
                plantId   = plant.plantaId,
                nombre    = plant.nombre,
                precio    = plant.precio,
                imagenUrl = plant.imagenUrl
            )
            cartRepository.addItem(item)
                .onSuccess {
                    _cartMessage.value = "${plant.nombre} agregado 🌿"
                }
                .onFailure {
                    _cartMessage.value = "Error al agregar al carrito"
                }
        }
    }

    fun clearCartMessage() { _cartMessage.value = null }

    fun logout() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
    }
}