package com.example.equipoocho.ui.util


import java.text.NumberFormat
import java.util.*


object Formatters {
    private val coLocale = Locale("es", "CO")
    private val currency = NumberFormat.getCurrencyInstance(coLocale)


    fun money(value: Double): String {
// Formato con separadores de miles y 2 decimales, incluye símbolo $ según locale
        return currency.format(value)
    }
}