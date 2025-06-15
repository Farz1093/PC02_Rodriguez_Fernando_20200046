package com.example.pc02_rodriguez_fernando_20200046.presentation.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ConversionScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("") }
    var toCurrency by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    // Leer tasas desde Firebase una vez
    LaunchedEffect(Unit) {
        db.collection("Conversion").document("conversion").get()
            .addOnSuccessListener { doc ->
                val data = doc.data?.mapValues { it.value.toString().toDouble() } ?: emptyMap()
                rates = data + ("USD" to 1.0)
                if (fromCurrency.isEmpty()) fromCurrency = "USD"
                if (toCurrency.isEmpty()) toCurrency = "EUR"
            }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Monto")
        TextField(value = amount, onValueChange = { amount = it })

        // FROM Dropdown
        Text("Desde:")
        Box {
            TextButton(onClick = { fromExpanded = true }) {
                Text(fromCurrency)
            }
            DropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                rates.keys.forEach { currency ->
                    DropdownMenuItem(onClick = {
                        fromCurrency = currency
                        fromExpanded = false
                    }, text = { Text(currency) })
                }
            }
        }

        // TO Dropdown
        Text("Hacia:")
        Box {
            TextButton(onClick = { toExpanded = true }) {
                Text(toCurrency)
            }
            DropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                rates.keys.forEach { currency ->
                    DropdownMenuItem(onClick = {
                        toCurrency = currency
                        toExpanded = false
                    }, text = { Text(currency) })
                }
            }
        }

        Button(onClick = {
            val input = amount.toDoubleOrNull()
            val fromRate = rates[fromCurrency]
            val toRate = rates[toCurrency]
            if (input != null && fromRate != null && toRate != null) {
                val converted = input / fromRate * toRate
                result = "$amount $fromCurrency ≈ ${"%.2f".format(converted)} $toCurrency"

                val conversion = hashMapOf(
                    "uid" to auth.currentUser?.uid,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "amount" to input,
                    "fromCurrency" to fromCurrency,
                    "toCurrency" to toCurrency,
                    "result" to converted
                )
                db.collection("conversions").add(conversion)
            }
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Convertir")
        }

        result?.let {
            Text(it, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
