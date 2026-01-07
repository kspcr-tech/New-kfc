package com.example.kfcvault.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "vault_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCardNumber(card: String) {
        prefs.edit().putString("card_number", card).apply()
    }

    fun getCardNumber(): String =
        prefs.getString("card_number", "") ?: ""

    fun saveBalance(balance: String) {
        prefs.edit().putString("balance", balance).apply()
    }

    fun getBalance(): String =
        prefs.getString("balance", "Not fetched")
}
