package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class OrdersActivity : AppCompatActivity() {

    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var adapter: OrderAdapter
    private lateinit var rvOrders: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        rvOrders    = findViewById(R.id.rvOrders)
        progressBar = findViewById(R.id.progressBarOrders)
        tvEmpty     = findViewById(R.id.tvEmptyOrders)
        val btnBack = findViewById<ImageButton>(R.id.btnBackOrders)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Crear adapter y asignar ANTES de observar
        adapter = OrderAdapter(emptyList())
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = adapter

        Log.d("OrdersActivity", "LayoutManager y adapter asignados")

        // Observers
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.orders.observe(this) { orders ->
            Log.d("OrdersActivity", "Orders recibidos: ${orders.size}")

            adapter.updateOrders(orders)

            Log.d("OrdersActivity", "Adapter count: ${adapter.itemCount}")
            Log.d("OrdersActivity", "RV height: ${rvOrders.height}")

            tvEmpty.visibility  = if (orders.isEmpty()) View.VISIBLE else View.GONE
            rvOrders.visibility = if (orders.isEmpty()) View.GONE    else View.VISIBLE

            adapter.notifyDataSetChanged()
            rvOrders.requestLayout()
            rvOrders.invalidate()
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Log.e("OrdersActivity", "Error: $it")
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loadOrders()
    }
}