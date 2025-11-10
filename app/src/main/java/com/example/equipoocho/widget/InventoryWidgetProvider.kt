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
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply { action = ACTION_TOGGLE }
        val piToggle = PendingIntent.getBroadcast(context, 200, toggleIntent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.btnToggle, piToggle)

        // Actualizar el saldo en el widget
        CoroutineScope(Dispatchers.IO).launch {
            val repo = InventoryRepository(context)
            val total = repo.total()  // Obtén el total del inventario
            val isVisible = session.isWidgetVisible()  // Verificar visibilidad del saldo
            val balanceText = if (isVisible) Formatters.money(total) else "$ ****"

            // Actualiza el widget con el saldo formateado
            views.setTextViewText(R.id.txtBalance, balanceText)

            // Cambiar el icono del botón de visibilidad (ojo)
            val eyeIcon = if (isVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
            views.setImageViewResource(R.id.btnToggle, eyeIcon)

            // Actualizar todos los widgets en la pantalla
            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    // Acción personalizada para alternar la visibilidad del widget
    companion object {
        const val ACTION_TOGGLE = "com.example.equipoocho.widget.TOGGLE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            // Alternar visibilidad
            val session = SessionManager(context)
            session.setWidgetVisible(!session.isWidgetVisible())
            updateAll(context)  // Actualiza el widget con la nueva visibilidad
        }
    }
}
