package com.plantify.plantify_app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantify.plantify_app.R

class FavoritosActivity : AppCompatActivity() {

    private val viewModel: FavoritosViewModel by viewModels()
    private lateinit var adapter: PlantAdapter
    private lateinit var rvFavoritos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        rvFavoritos = findViewById(R.id.rvFavoritos)
        progressBar = findViewById(R.id.progressBarFavoritos)
        tvEmpty     = findViewById(R.id.tvEmptyFavoritos)
        val btnBack = findViewById<ImageButton>(R.id.btnBackFavoritos)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = PlantAdapter(emptyList()) { plant ->
            // Toca la tarjeta → abre detalle
            val intent = Intent(this, PlantDetailActivity::class.java)
            intent.putExtra("plantaId", plant.plantaId)
            startActivity(intent)
        }

        rvFavoritos.layoutManager = GridLayoutManager(this, 2)
        rvFavoritos.adapter = adapter

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.favoritos.observe(this) { plants ->
            adapter.updatePlants(plants)
            tvEmpty.visibility      = if (plants.isEmpty()) View.VISIBLE else View.GONE
            rvFavoritos.visibility  = if (plants.isEmpty()) View.GONE    else View.VISIBLE
        }

        viewModel.message.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        viewModel.loadFavoritos()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoritos()
    }
}