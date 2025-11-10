package com.example.equipoocho.ui.login


import android.content.Context


class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
    fun isLogged(): Boolean = prefs.getBoolean("logged", false)
    fun setLogged(v: Boolean) { prefs.edit().putBoolean("logged", v).apply() }


    // Visibilidad widget (saldo oculto/visible)
    fun isWidgetVisible(): Boolean = prefs.getBoolean("widget_visible", false)
    fun setWidgetVisible(v: Boolean) { prefs.edit().putBoolean("widget_visible", v).apply() }
}