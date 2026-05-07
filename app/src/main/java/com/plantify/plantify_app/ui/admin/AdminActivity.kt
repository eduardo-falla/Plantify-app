package com.plantify.plantify_app.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.plantify.plantify_app.R
import com.plantify.plantify_app.ui.login.LoginActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        bottomNav = findViewById(R.id.bottomNavAdmin)

        // Fragment inicial
        if (savedInstanceState == null) {
            loadFragment(AdminDashboardFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> {
                    loadFragment(AdminDashboardFragment())
                    true
                }
                R.id.nav_admin_plantas -> {
                    loadFragment(AdminPlantasFragment())
                    true
                }
                R.id.nav_admin_pedidos -> {
                    loadFragment(AdminPedidosFragment())
                    true
                }
                R.id.nav_admin_perfil -> {
                    loadFragment(AdminPerfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.adminFragmentContainer, fragment)
            .commit()
    }

    fun logout() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}