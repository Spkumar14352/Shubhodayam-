package com.example.utilities.ui

import com.example.data.model.Language
import com.example.utilities.calc.IndianFormat

/** Telugu or English text for in-screen labels. */
fun Language.t(te: String, en: String) = if (this == Language.TELUGU) te else en

fun rupees(value: Double, decimals: Int = 0) = IndianFormat.rupees(value, decimals)

fun num(text: String) = IndianFormat.parse(text)
