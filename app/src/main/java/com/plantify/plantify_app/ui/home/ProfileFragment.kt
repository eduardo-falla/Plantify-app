package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plantify.plantify_app.R

class ProfileFragment : Fragment() {

    private fun mostrarDialogoCambiarPassword() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            android.R.layout.two_line_list_item, null
        )
        val etPassword = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Nueva contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etConfirm = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Confirmar contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(etPassword)
            addView(etConfirm)
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cambiar contraseña")
            .setView(layout)
            .setPositiveButton("Aceptar") { _, _ ->
                val password = etPassword.text.toString()
                val confirm  = etConfirm.text.toString()
                when {
                    password.isEmpty() || confirm.isEmpty() ->
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    password != confirm ->
                        Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    password.length < 6 ->
                        Toast.makeText(requireContext(), "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                    else -> {
                        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            ?.updatePassword(password)
                            ?.addOnSuccessListener {
                                Toast.makeText(requireContext(), "Contraseña actualizada ✅", Toast.LENGTH_SHORT).show()
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoCambiarCorreo() {
        val etCorreo = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Nuevo correo electrónico"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(etCorreo)
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cambiar correo")
            .setView(layout)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevoCorreo = etCorreo.text.toString().trim()
                if (nuevoCorreo.isEmpty()) {
                    Toast.makeText(requireContext(), "Ingresa un correo", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    ?.verifyBeforeUpdateEmail(nuevoCorreo)
                    ?.addOnSuccessListener {
                        Toast.makeText(requireContext(),
                            "Se envió un correo de verificación a $nuevoCorreo", Toast.LENGTH_LONG).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEliminarCuenta() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar cuenta")
            .setMessage("¿Realmente deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    ?.delete()
                    ?.addOnSuccessListener {
                        Toast.makeText(requireContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                        (activity as? HomeActivity)?.logout()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombrePerfil)
        val tvEmail  = view.findViewById<TextView>(R.id.tvEmailPerfil)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val nombre   = doc.getString("nombre") ?: ""
                    val apellido = doc.getString("apellido") ?: ""
                    val email    = doc.getString("email") ?: ""
                    tvNombre.text = "$nombre $apellido"
                    tvEmail.text  = email
                }
        }

        view.findViewById<View>(R.id.btnHistorial).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistorialFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.btnFavoritos).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FavoritoFragment())
                .addToBackStack(null)
                .commit()
        }

        // En onCreate después de los botones existentes
        view.findViewById<View>(R.id.btnCambiarPassword).setOnClickListener {
            mostrarDialogoCambiarPassword()
        }

        view.findViewById<View>(R.id.btnCambiarCorreo).setOnClickListener {
            mostrarDialogoCambiarCorreo()
        }

        view.findViewById<View>(R.id.btnEliminarCuenta).setOnClickListener {
            mostrarDialogoEliminarCuenta()
        }
        val btnLogout = view.findViewById<View>(R.id.btnCardLogout)
        btnLogout.setOnClickListener {
            (activity as? HomeActivity)?.logout()
        }

        return view
    }
}