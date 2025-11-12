package com.example.equipoocho.ui.login


import android.content.Context


// Clase para manejar la sesión del usuario y la visibilidad del widget
class SessionManager(context: Context) {

    // Se usa SharedPreferences para guardar datos simples de sesión
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
    // Verifica si el usuario ya inició sesión (true = logueado)
    fun isLogged(): Boolean = prefs.getBoolean("logged", false)
    // Guarda el estado de la sesión (true para iniciar sesión, false para cerrarla)
    fun setLogged(v: Boolean) { prefs.edit().putBoolean("logged", v).apply() }


    // Visibilidad widget (saldo oculto/visible)
    fun isWidgetVisible(): Boolean = prefs.getBoolean("widget_visible", false)
    // Guarda el estado de visibilidad del widget (true = visible, false = oculto)
    fun setWidgetVisible(v: Boolean) { prefs.edit().putBoolean("widget_visible", v).apply() }
}