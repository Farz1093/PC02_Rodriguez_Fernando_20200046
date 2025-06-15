package com.example.pc02_rodriguez_fernando_20200046.presentation.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ConversionScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var result by remember { mutableStateOf<String?>(null) }
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    // Leer tasas desde Firebase
    LaunchedEffect(true) {
        db.collection("Conversion").document("conversion").get()
            .addOnSuccessListener { doc ->
                val loadedRates = doc.data?.mapValues { it.value.toString().toDouble() } ?: emptyMap()
                rates = loadedRates + ("USD" to 1.0)  // Agrega USD como base
            }
    }

    if (rates.isEmpty()) {
        Text("Cargando tasas de cambio...")
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Monto") }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Desde:")
        CurrencyDropdown(rates.keys.toList(), fromCurrency) { fromCurrency = it }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Hacia:")
        CurrencyDropdown(rates.keys.toList(), toCurrency) { toCurrency = it }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            val input = amount.toDoubleOrNull()
            if (input != null) {
                val fromRate = rates[fromCurrency] ?: 1.0
                val toRate = rates[toCurrency] ?: 1.0
                val converted = input / fromRate * toRate
                result = "$amount $fromCurrency = ${"%.2f".format(converted)} $toCurrency"
            }
        }) {
            Text("Convertir")
        }

        result?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login") {
                popUpTo("conversion") { inclusive = true }
            }
        }) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun CurrencyDropdown(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(selected, modifier = Modifier
            .clickable { expanded = true }
            .padding(8.dp))

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(onClick = {
                    onSelected(it)
                    expanded = false
                }, text = { Text(it) })
            }
        }
    }
}
