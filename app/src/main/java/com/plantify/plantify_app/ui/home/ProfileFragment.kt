package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R

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

        val tvNombre     = view.findViewById<TextView>(R.id.tvUserName)
        val tvEmail      = view.findViewById<TextView>(R.id.tvUserEmail)
        val tvRol        = view.findViewById<TextView>(R.id.tvUserRol)
        val ivFoto       = view.findViewById<ImageView>(R.id.ivUserPhoto)
        val progressBar  = view.findViewById<ProgressBar>(R.id.progressBarProfile)
        val btnLogout    = view.findViewById<View>(R.id.btnCardLogout)

        // Logout
        btnLogout.setOnClickListener {
            (activity as? HomeActivity)?.logout()
        }

        // Observar datos del usuario
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            tvNombre.text = usuario.nombre.ifEmpty { "Sin nombre" }
            tvEmail.text  = usuario.email
            tvRol.text    = when (usuario.rol) {
                "admin"     -> "👑 Administrador"
                "comprador" -> "🛒 Comprador"
                else        -> usuario.rol
            }

            // Foto de perfil
            if (usuario.fotoPerfil.isNotEmpty()) {
                Glide.with(this)
                    .load(usuario.fotoPerfil)
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivFoto)
            }
        }
        // Editar perfil
        view.findViewById<View>(R.id.cardEditarPerfil).setOnClickListener {
            startActivity(
                android.content.Intent(requireContext(), EditProfileActivity::class.java)
            )
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar?.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loadProfile()
    }
}