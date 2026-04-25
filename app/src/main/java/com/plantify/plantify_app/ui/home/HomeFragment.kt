package com.plantify.plantify_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.plantify.plantify_app.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tvVerTodo = view.findViewById<TextView>(R.id.tvVerTodo)
        tvVerTodo.setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        val searchBar = view.findViewById<View>(R.id.searchBarClickable)
        searchBar.setOnClickListener {
            (activity as? HomeActivity)?.navigateToSearch()
        }

        return view
    }
}