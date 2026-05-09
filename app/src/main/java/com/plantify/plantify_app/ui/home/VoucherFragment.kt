package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.plantify.plantify_app.R
import com.plantify.plantify_app.model.CartItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VoucherFragment : Fragment(R.layout.fragment_voucher) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvProductos = view.findViewById<TextView>(R.id.tvVoucherProductos)
        val tvTotal     = view.findViewById<TextView>(R.id.tvVoucherTotal)
        val tvFecha     = view.findViewById<TextView>(R.id.tvVoucherFecha)
        val btnVolver   = view.findViewById<Button>(R.id.btnVolverInicio)

        val total = arguments?.getDouble("total") ?: 0.0
        @Suppress("UNCHECKED_CAST")
        val items = arguments?.getSerializable("items") as? ArrayList<CartItem> ?: arrayListOf()

        tvTotal.text = "S/ ${"%.2f".format(total)}"

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        tvFecha.text = sdf.format(Date())

        val productos = items.joinToString("\n") {
            "• ${it.nombre} x${it.cantidad} — S/ ${"%.2f".format(it.precio * it.cantidad)}"
        }
        tvProductos.text = productos

        btnVolver.setOnClickListener {
            // Limpia todo el backstack y vuelve al Home
            parentFragmentManager.popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    companion object {
        fun newInstance(total: Double, items: List<CartItem>) = VoucherFragment().apply {
            arguments = Bundle().apply {
                putDouble("total", total)
                putSerializable("items", ArrayList(items))
            }
        }
    }
}