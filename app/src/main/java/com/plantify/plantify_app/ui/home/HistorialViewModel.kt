package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.PedidoRepository
import com.plantify.plantify_app.model.Pedido
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val repository: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _pedidos = MutableLiveData<List<Pedido>>()
    val pedidos: LiveData<List<Pedido>> = _pedidos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadPedidos() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            repository.getPedidosFlow().collect { pedidos ->
                _pedidos.postValue(pedidos)
                _isLoading.postValue(false)
            }
        }
    }
}