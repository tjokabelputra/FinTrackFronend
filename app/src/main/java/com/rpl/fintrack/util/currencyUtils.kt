package com.rpl.fintrack.util

import java.text.NumberFormat
import java.util.Locale

object currencyUtils {
    fun formatRupiah(amount: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        numberFormat.minimumFractionDigits = 0
        return numberFormat.format(amount)
    }
}