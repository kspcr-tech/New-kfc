package com.example.kfcvault.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kfcvault.security.SecureStorage
import com.example.kfcvault.sms.SmsBalanceReader

@Composable
fun GiftCardScreen() {

    val context = LocalContext.current
    val activity = context as ComponentActivity
    val storage = remember { SecureStorage(context) }

    var cardNumber by remember { mutableStateOf(storage.getCardNumber()) }
    var cardPin by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }
    var balance by remember { mutableStateOf(storage.getBalance()) }
    var statusMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "KFC Gift Card Vault",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        /** Gift Card Number **/
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Gift Card Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        /** Gift Card PIN **/
        OutlinedTextField(
            value = cardPin,
            onValueChange = { cardPin = it },
            label = { Text("Gift Card PIN") },
            visualTransformation = if (showPin)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPin = !showPin }) {
                    Icon(
                        imageVector = if (showPin)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle PIN visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        /** Save Button **/
        Button(
            onClick = {
                storage.saveCardNumber(cardNumber)
                statusMessage = "Gift card saved securely"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Gift Card")
        }

        Spacer(modifier = Modifier.height(24.dp))

        /** Fetch Balance **/
        Button(
            onClick = {

                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_SMS),
                        2001
                    )
                    statusMessage = "Please allow SMS permission"
                } else {
                    val result = SmsBalanceReader.fetchLatestKfcBalance(context)
if (result != null) {
    balance = result.balance
    storage.saveBalance(result.balance)
    statusMessage = "Expiry: ${result.expiry}"
                    } else {
                        statusMessage = "No KFC balance SMS found"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Balance from SMS")
        }

        Spacer(modifier = Modifier.height(16.dp))

        /** Balance Display **/
        Text(
            text = "Balance: $balance",
            style = MaterialTheme.typography.bodyLarge
        )

        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}                    )
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
