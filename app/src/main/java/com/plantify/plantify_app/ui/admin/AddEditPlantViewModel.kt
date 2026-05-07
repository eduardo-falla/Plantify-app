package com.plantify.plantify_app.ui.admin

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.plantify.plantify_app.data.PlantRepository
import com.plantify.plantify_app.model.Plant
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddEditPlantViewModel(
    private val repository: PlantRepository = PlantRepository()
) : ViewModel() {

    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _plant = MutableLiveData<Plant>()
    val plant: LiveData<Plant> = _plant

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPlant(plantaId: String) {
        viewModelScope.launch {
            repository.getPlantById(plantaId)
                .onSuccess { _plant.value = it }
                .onFailure { _error.value = "Error al cargar planta" }
        }
    }

    fun savePlant(
        plantaId: String,
        nombre: String,
        descripcion: String,
        cuidados: String,
        precio: Double,
        stock: Int,
        categoria: String,
        imageUri: Uri?,
        esEdicion: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Subir imagen si se seleccionó una nueva
                val imagenUrl = if (imageUri != null) {
                    val ref = storage.reference
                        .child("plantas/${System.currentTimeMillis()}.jpg")
                    ref.putFile(imageUri).await()
                    ref.downloadUrl.await().toString()
                } else {
                    _plant.value?.imagenUrl ?: ""
                }

                val plant = Plant(
                    plantaId    = if (esEdicion) plantaId else "",
                    nombre      = nombre,
                    descripcion = descripcion,
                    cuidados    = cuidados,
                    precio      = precio,
                    stock       = stock,
                    categoria   = categoria,
                    imagenUrl   = imagenUrl
                )

                if (esEdicion) {
                    // Actualizar
                    db.collection("plantas")
                        .document(plantaId)
                        .set(plant).await()
                } else {
                    // Crear nuevo
                    val ref = db.collection("plantas").document()
                    ref.set(plant.copy(plantaId = ref.id)).await()
                }

                _success.value = true

            } catch (e: Exception) {
                _error.value = "Error al guardar: ${e.message}"
            }
            _isLoading.value = false
        }
    }
}