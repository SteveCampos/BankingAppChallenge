package com.stevecampos.core.ui.util

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(
    amount: Double,
    currency: String,
): String {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "PE"))
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return "$currency ${formatter.format(amount)}"
}
