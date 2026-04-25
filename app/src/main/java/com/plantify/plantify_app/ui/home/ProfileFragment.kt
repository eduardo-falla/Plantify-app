package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.plantify.plantify_app.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnLogout = view.findViewById<View>(R.id.btnCardLogout)

        btnLogout.setOnClickListener {
            (activity as? HomeActivity)?.logout()
        }

        return view
    }
}