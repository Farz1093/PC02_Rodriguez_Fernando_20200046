package com.example.pc02_rodriguez_fernando_20200046

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pc02_rodriguez_fernando_20200046.presentation.auth.presentation.ConversionScreen
import com.example.pc02_rodriguez_fernando_20200046.presentation.auth.presentation.LoginScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = auth.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "conversion" else "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("conversion") {
            ConversionScreen(navController)
        }
    }
}
