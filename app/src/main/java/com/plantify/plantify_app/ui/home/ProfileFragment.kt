package com.plantify.plantify_app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre         = view.findViewById<TextView>(R.id.tvUserName)
        val tvEmail          = view.findViewById<TextView>(R.id.tvUserEmail)
        val tvRol            = view.findViewById<TextView>(R.id.tvUserRol)
        val ivFoto           = view.findViewById<ImageView>(R.id.ivUserPhoto)
        val progressBar      = view.findViewById<ProgressBar>(R.id.progressBarProfile)
        val btnLogout        = view.findViewById<View>(R.id.btnCardLogout)
        val btnEliminar      = view.findViewById<View>(R.id.btnCardEliminarCuenta)
        val cardEditarPerfil = view.findViewById<View>(R.id.cardEditarPerfil)
        val cardMisPedidos   = view.findViewById<View>(R.id.cardMisPedidos)
        val cardFavoritos    = view.findViewById<View>(R.id.cardFavoritos)

        // Logout
        btnLogout.setOnClickListener {
            (activity as? HomeActivity)?.logout()
        }

        // Editar perfil
        cardEditarPerfil.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // Mis pedidos
        cardMisPedidos.setOnClickListener {
            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }

        // ── Favoritos ─────────────────────────────────────────
        cardFavoritos.setOnClickListener {
            startActivity(Intent(requireContext(), FavoritosActivity::class.java))
        }

        // Eliminar cuenta
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Eliminar cuenta")
                .setMessage("¿Estás seguro? Esta acción eliminará tu cuenta, datos y carrito permanentemente. No se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.eliminarCuenta()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Observers
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            tvNombre.text = usuario.nombre.ifEmpty { "Sin nombre" }
            tvEmail.text  = usuario.email
            tvRol.text    = when (usuario.rol) {
                "admin"     -> "👑 Administrador"
                "comprador" -> "🛒 Comprador"
                else        -> usuario.rol
            }
            if (usuario.fotoPerfil.isNotEmpty()) {
                Glide.with(this)
                    .load(usuario.fotoPerfil)
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivFoto)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar?.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.cuentaEliminada.observe(viewLifecycleOwner) { eliminada ->
            if (eliminada == true) {
                Toast.makeText(requireContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loadProfile()
    }
}