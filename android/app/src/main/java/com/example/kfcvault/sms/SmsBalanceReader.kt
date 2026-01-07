package com.example.kfcvault.sms

import android.content.Context
import android.net.Uri

object SmsBalanceReader {

    fun fetchLatestKfcBalance(context: Context): String? {
        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("body"),
            "body LIKE ?",
            arrayOf("%KFC%"),
            "date DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val body = it.getString(0)

                // Example SMS:
                // "Your KFC Gift Card balance is Rs.325.50"
                val regex = Regex("(Rs\\.?|₹)(\\d+(\\.\\d+)?)")
                val match = regex.find(body)

                return match?.value
            }
        }
        return null
    }
}
