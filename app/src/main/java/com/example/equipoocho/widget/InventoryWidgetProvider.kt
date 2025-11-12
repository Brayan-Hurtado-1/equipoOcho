package com.example.equipoocho.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.equipoocho.R
import com.example.equipoocho.ui.login.LoginActivity
import com.example.equipoocho.ui.login.SessionManager
import com.example.equipoocho.ui.util.Formatters
import com.example.equipoocho.data.repo.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Cargar todos los widgets en la pantalla
        updateAll(context)
    }

    private fun updateAll(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, InventoryWidgetProvider::class.java))
        val session = SessionManager(context)

        // Crear el RemoteViews para actualizar el widget
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        // Acción para abrir la pantalla de Login cuando se hace clic en "Gestionar Inventario"
        val piManage = PendingIntent.getActivity(
            context, 100,
            Intent(context, LoginActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnManage, piManage)

        // Acción para alternar la visibilidad del saldo
        // Aquí creamos un Intent para el usuario toque el icono del ojo del widget
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply { action = ACTION_TOGGLE }
        val piToggle = PendingIntent.getBroadcast(context, 200, toggleIntent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.btnToggle, piToggle)

        // Usamos una corrutina en hilo de fondo (Dispatchers.IO) porque el repositorio accede a la base de datos.
        CoroutineScope(Dispatchers.IO).launch {
            val repo = InventoryRepository(context)
            val total = repo.total()  // Obtiene el total del inventario desde la base de datos
            val isVisible = session.isWidgetVisible()  // Verificar visibilidad del saldo
             // Si el saldo es visible se muestra formateado, si no, se reemplaza con ****
            val balanceText = if (isVisible) Formatters.money(total) else "$ ****"

            // Actualiza el widget con el saldo formateado
            views.setTextViewText(R.id.txtBalance, balanceText)

            // Cambiamos el icono del botón del ojo según el estado actual
            // - Si el saldo está visible, mostramos el icono de ojo cerrado (para ocultar)
            // - Si el saldo está oculto, mostramos el icono de ojo abierto (para mostrar)
            val eyeIcon = if (isVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
            views.setImageViewResource(R.id.btnToggle, eyeIcon)

            // Actualizamos todos los widgets en la pantalla con la nueva vista
            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    // Acción personalizada para alternar la visibilidad del widget en cuanto se hace click sobre el boton del ojo
    companion object {
        const val ACTION_TOGGLE = "com.example.equipoocho.widget.TOGGLE"
    }
    // Este es el "listener" que responde en cuanto se clickea sobre el boton
    // Alternando asi el icono del ojo para luego llamar al "update" y actualizar los widgets
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            // Usamos SessionManager para cambiar el valor guardado.
            // Esto se almacena en SharedPreferences, así que el estado persiste
            // incluso si el usuario reinicia el teléfono o cierra la app.
            val session = SessionManager(context)
            session.setWidgetVisible(!session.isWidgetVisible())
            updateAll(context)  // Actualiza el widget con la nueva visibilidad
        }
    }
}
