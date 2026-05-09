package com.plantify.plantify_app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.plantify.plantify_app.R
import com.plantify.plantify_app.data.AuthRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AdminPerfilFragment : Fragment() {

    private val repository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivFoto   = view.findViewById<ImageView>(R.id.ivAdminPhoto)
        val tvNombre = view.findViewById<TextView>(R.id.tvAdminNombre)
        val tvEmail  = view.findViewById<TextView>(R.id.tvAdminEmail)
        val tvRol    = view.findViewById<TextView>(R.id.tvAdminRol)
        val tvUid    = view.findViewById<TextView>(R.id.tvAdminUid)
        val btnLogout = view.findViewById<View>(R.id.btnAdminLogout)

        // Cerrar sesión
        btnLogout.setOnClickListener {
            (activity as? AdminActivity)?.logout()
        }

        // Cargar datos del admin desde Firestore
        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = repository.getCurrentUserFull()
            usuario?.let {
                tvNombre.text = it.nombre.ifEmpty { "Administrador" }
                tvEmail.text  = it.email
                tvRol.text    = "👑 Administrador"
                tvUid.text    = "ID: ${it.uid.take(16)}..."

                if (it.fotoPerfil.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(it.fotoPerfil)
                        .circleCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(ivFoto)
                }
            }
        }
    }
}