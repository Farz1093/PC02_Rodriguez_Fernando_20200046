package com.example.pc02_rodriguez_fernando_20200046.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pc02_rodriguez_fernando_20200046.presentation.auth.presentation.ConversionScreen
import com.example.pc02_rodriguez_fernando_20200046.presentation.auth.presentation.LoginScreen

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("conversion") {
            ConversionScreen()
        }
    }
}
