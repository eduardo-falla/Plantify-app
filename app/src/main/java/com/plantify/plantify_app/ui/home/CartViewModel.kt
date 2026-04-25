package com.plantify.plantify_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantify.plantify_app.data.CartRepository
import com.plantify.plantify_app.model.CartItem
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository = CartRepository()
) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    fun loadCart() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val result = repository.getCartItems()
            result.onSuccess { items ->
                _cartItems.postValue(items)
                _total.postValue(items.sumOf { it.precio * it.quantity })
            }
            _isLoading.postValue(false)
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            repository.removeItem(itemId)
            loadCart()
        }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(itemId, quantity)
            loadCart()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            loadCart()
        }
    }
}