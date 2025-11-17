package com.example.equipoOcho.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.example.equipoOcho.R
import com.example.equipoOcho.data.InventoryDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import com.example.equipoOcho.view.MainActivity

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {

        const val ACTION_TOGGLE_VISIBILITY =
            "com.example.equipoOcho.action.TOGGLE_VISIBILITY"

        private const val PREFS_NAME = "inventory_widget_prefs"
        private const val KEY_PREFIX = "show_balance_"

        private fun prefs(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        private fun isBalanceVisible(context: Context, appWidgetId: Int): Boolean =
            prefs(context).getBoolean(KEY_PREFIX + appWidgetId, false)

        private fun setBalanceVisible(
            context: Context,
            appWidgetId: Int,
            visible: Boolean
        ) {
            prefs(context).edit()
                .putBoolean(KEY_PREFIX + appWidgetId, visible)
                .apply()
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // 1. Obtener total inventario desde Room (bloqueante pero simple)
            val total = runBlocking(Dispatchers.IO) {
                val dao = InventoryDB.getDatabase(context).inventoryDao()
                dao.getTotalInventoryValue() ?: 0.0
            }

            val views = RemoteViews(context.packageName, R.layout.widget_inventory)

            // 2. Textos fijos
            views.setTextViewText(R.id.tvQuestion, "¿Cuánto tengo de inventario?")
            views.setTextViewText(R.id.tvInventoryTitle, "Inventory")
            views.setTextViewText(R.id.tvManage, "Gestionar inventario")

            // 3. Formateo del saldo: 3.326,00
            val formatter = DecimalFormat("#,##0.00").apply {
                decimalFormatSymbols = decimalFormatSymbols.apply {
                    groupingSeparator = '.'
                    decimalSeparator = ','
                }
            }
            val formatted = formatter.format(total)

            // 4. Mostrar saldo o **** según preferencia
            val showBalance = isBalanceVisible(context, appWidgetId)

            if (showBalance) {
                views.setTextViewText(R.id.tvBalance, "$ $formatted")
                views.setImageViewResource(R.id.ivEye, R.drawable.ic_eye_closed_white)
            } else {
                views.setTextViewText(R.id.tvBalance, "$ ****")
                views.setImageViewResource(R.id.ivEye, R.drawable.ic_eye_open_white)
            }

            // 5. CLICK EN EL OJO → BROADCAST PARA TOGGLE
            val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_VISIBILITY
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId, // requestCode único
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.ivEye, togglePendingIntent)

            // 6. CLICK EN “Gestionar inventario” → abrir MainActivity (login)
            val manageIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("open_login_from_widget", true)   // ⬅️ MARCADOR
            }

            val managePendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId + 1000,
                manageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.ivManageIcon, managePendingIntent)
            views.setOnClickPendingIntent(R.id.tvManage, managePendingIntent)


            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, InventoryWidgetProvider::class.java)

        when (intent.action) {
            ACTION_TOGGLE_VISIBILITY -> {
                val appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val current = isBalanceVisible(context, appWidgetId)
                    setBalanceVisible(context, appWidgetId, !current)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                } else {
                    // Por si acaso, actualizar todos
                    val ids = appWidgetManager.getAppWidgetIds(thisWidget)
                    ids.forEach { id ->
                        val current = isBalanceVisible(context, id)
                        setBalanceVisible(context, id, !current)
                        updateAppWidget(context, appWidgetManager, id)
                    }
                }
            }
        }
    }
}
