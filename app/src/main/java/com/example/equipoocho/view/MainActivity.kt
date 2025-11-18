package com.example.equipoOcho.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.equipoOcho.R
import com.example.equipoOcho.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ¿Viene del widget?
        val fromWidget = intent?.getBooleanExtra("open_login_from_widget", false) ?: false

        // Si NO viene del widget, cerramos y no mostramos nada
        if (!fromWidget) {
            finish()
            return
        }

        // Solo si viene del widget inicializamos la UI
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigationContainer) as NavHostFragment
        navController = navHostFragment.navController

        // Cargamos el gráfico de navegación
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        // Si la sesión está activa → ir directo a Home
        if (SessionManager.isSessionActive(this)) {
            navGraph.setStartDestination(R.id.homeInventoryFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }

        navController.graph = navGraph
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val fromWidget = intent.getBooleanExtra("open_login_from_widget", false)
        if (fromWidget && ::navController.isInitialized) {
            if (navController.currentDestination?.id != R.id.loginFragment) {
                navController.navigate(R.id.loginFragment)
            }
        } else if (!fromWidget) {
            // Si llega un intent que NO viene del widget, cerramos
            finish()
        }
    }
}