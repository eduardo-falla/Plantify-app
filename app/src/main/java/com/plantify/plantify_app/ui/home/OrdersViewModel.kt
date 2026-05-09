package com.plantify.plantify_app.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.OrderRepository
import com.plantify.plantify_app.model.Order
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMyOrders()
                .onSuccess { orders ->
                    Log.d("Orders", "Pedidos encontrados: ${orders.size}")
                    orders.forEachIndexed { i, order ->
                        Log.d("Orders", "Pedido $i: ${order.pedidoId} - ${order.estado}")
                    }
                    _orders.value = orders
                }
                .onFailure { e ->
                    Log.e("Orders", "Error: ${e.message}")
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }
}