package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.PlantRepository
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class SearchViewModel(
    private val repository: PlantRepository = PlantRepository()
) : ViewModel() {

    private val _results = MutableLiveData<List<Plant>>()
    val results: LiveData<List<Plant>> = _results

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Guarda todas las plantas para filtrar localmente
    private var allPlants: List<Plant> = emptyList()
    private var currentQuery: String = ""
    private var currentCategory: String = "Todas"

    // Job para debounce de búsqueda
    private var searchJob: Job? = null

    fun loadAllPlants() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPlants()
                .onSuccess { plants ->
                    allPlants = plants
                    applyFilters()
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Error al cargar plantas"
                }
            _isLoading.value = false
        }
    }

    // Búsqueda con debounce de 300ms
    fun search(query: String) {
        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            applyFilters()
        }
    }

    fun filterByCategory(categoria: String) {
        currentCategory = categoria
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = allPlants

        // Filtrar por categoría
        if (currentCategory != "Todas") {
            filtered = filtered.filter { it.categoria == currentCategory }
        }

        // Filtrar por búsqueda
        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.nombre.contains(currentQuery, ignoreCase = true) ||
                        it.categoria.contains(currentQuery, ignoreCase = true) ||
                        it.descripcion.contains(currentQuery, ignoreCase = true)
            }
        }

        _results.value = filtered
    }

    fun getCategories(): List<String> {
        val cats = mutableListOf("Todas")
        cats.addAll(allPlants.map { it.categoria }.distinct())
        return cats
    }
}