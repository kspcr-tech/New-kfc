package com.example.kfcvault.sms

import android.content.Context
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

object SmsBalanceReader {

    data class BalanceResult(
        val balance: String,
        val expiry: String
    )

    fun fetchLatestKfcBalance(context: Context): BalanceResult? {

        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("address", "body"),
            "address = ?",
            arrayOf("55757575"),
            "date DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val body = it.getString(it.getColumnIndexOrThrow("body"))

                // Balance regex (₹ or Rs)
                val balanceRegex = Regex("(Rs\\.?|₹)\\s?(\\d+(\\.\\d+)?)")

                // Expiry regex (dd/MM/yyyy or dd-MM-yyyy)
                val expiryRegex = Regex(
                    "(Expiry|Valid till)\\s*[:]?\\s*([0-9]{2}[-/][0-9]{2}[-/][0-9]{4})"
                )

                val balanceMatch = balanceRegex.find(body)
                val expiryMatch = expiryRegex.find(body)

                val balance = balanceMatch?.value ?: "Not found"
                val rawExpiry = expiryMatch?.groupValues?.get(2)

                val formattedExpiry = rawExpiry?.let {
                    formatDateToMMM(it)
                } ?: "Not found"

                return BalanceResult(
                    balance = balance,
                    expiry = formattedExpiry
                )
            }
        }
        return null
    }

    private fun formatDateToMMM(input: String): String {
        val inputFormats = listOf("dd/MM/yyyy", "dd-MM-yyyy")
        val outputFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH)

        for (format in inputFormats) {
            try {
                val parser = SimpleDateFormat(format, Locale.ENGLISH)
                val date = parser.parse(input)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (_: Exception) {
                // try next format
            }
        }
        return input // fallback (should not happen)
    }
}
