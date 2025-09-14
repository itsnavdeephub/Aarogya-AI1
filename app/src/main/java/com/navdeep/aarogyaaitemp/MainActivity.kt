package com.navdeep.aarogyaaitemp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.navdeep.aarogyaaitemp.ui.theme.AarogyaAITempTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AarogyaAITempTheme {
                MyApp()
            }

        }
    }
}
@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "chatScreen") {

        composable("chatScreen") {
            ChatScreen(
                onNavigateToSecond = { navController.navigate("secondScreen") },
                onNavigateToInfo = { navController.navigate("infoScreen") }
            )
        }

        composable("secondScreen") {
            SecondScreen(
                onNavigateToChat = { navController.navigate("chatScreen") },
                onNavigateToInfo = { navController.navigate("infoScreen") }
            )
        }

        composable("infoScreen") {
            infoScreen(
                onNavigateToChat = { navController.navigate("chatScreen") },
                onNavigateToSecond = { navController.navigate("secondScreen") }
            )
        }
    }
}

