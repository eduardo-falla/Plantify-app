package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class CartFragment : Fragment(R.layout.fragment_cart) {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCartItems   = view.findViewById<RecyclerView>(R.id.rvCartItems)
        val tvTotal       = view.findViewById<TextView>(R.id.tvTotal)
        val tvSubtotal    = view.findViewById<TextView>(R.id.tvSubtotal)
        val tvCartCount   = view.findViewById<TextView>(R.id.tvCartCount)
        val btnCheckout   = view.findViewById<Button>(R.id.btnCheckout)
        val progressBar   = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty       = view.findViewById<TextView>(R.id.tvEmpty)
        val rvCart        = view.findViewById<View>(R.id.rvCart)

        val SHIPPING = 5.99

        adapter = CartAdapter(
            items = emptyList(),
            onIncrease = { item -> viewModel.updateQuantity(item.id, item.quantity + 1) },
            onDecrease = { item -> viewModel.updateQuantity(item.id, item.quantity - 1) },
            onRemove   = { item -> viewModel.removeItem(item.id) }
        )

        rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        rvCartItems.adapter = adapter

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)

            val isEmpty = items.isEmpty()
            tvEmpty.visibility   = if (isEmpty) View.VISIBLE else View.GONE
            rvCart.visibility    = if (isEmpty) View.GONE else View.VISIBLE

            val count = items.sumOf { it.quantity }
            tvCartCount.text = "$count ${if (count == 1) "artículo" else "artículos"}"
        }

        viewModel.total.observe(viewLifecycleOwner) { subtotal ->
            val total = subtotal + SHIPPING
            tvSubtotal.text = "S/ ${"%.2f".format(subtotal)}"
            tvTotal.text    = "S/ ${"%.2f".format(total)}"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        btnCheckout.setOnClickListener {
            // Aquí conectar el flujo de pago
        }

        viewModel.loadCart()
    }
}