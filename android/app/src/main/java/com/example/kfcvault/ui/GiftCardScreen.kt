package com.example.kfcvault.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import com.example.kfcvault.security.SecureStorage
import com.example.kfcvault.sms.SmsBalanceReader

@Composable
fun GiftCardScreen() {

    val context = LocalContext.current
    val activity = context as ComponentActivity
    val storage = remember { SecureStorage(context) }

    var isVisible by remember { mutableStateOf(false) }
    var giftCardNumber by remember {
        mutableStateOf(
            storage.getCardNumber().ifEmpty { "1234 5678 9012 3456" }
        )
    }
    var balance by remember { mutableStateOf(storage.getBalance()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "KFC Vault – Phase 1b",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = giftCardNumber,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gift Card Number") },
            visualTransformation = if (isVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isVisible = !isVisible }) {
                    Icon(
                        imageVector = if (isVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Save card securely (Phase 1b-A)
                storage.saveCardNumber(giftCardNumber)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Card Securely")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                // Runtime SMS permission check
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_SMS),
                        1001
                    )
                } else {
                    // Fetch balance from SMS (Phase 1b-B)
                    val fetched = SmsBalanceReader.fetchLatestKfcBalance(context)
                    if (fetched != null) {
                        balance = fetched
                        storage.saveBalance(fetched)
                    } else {
                        balance = "No KFC balance SMS found"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Balance from SMS")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Balance: $balance",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
