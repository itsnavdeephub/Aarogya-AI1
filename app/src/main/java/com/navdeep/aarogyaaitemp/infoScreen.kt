package com.navdeep.aarogyaaitemp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun infoScreen(onNavigateToSecond: () -> Unit, onNavigateToChat: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Aarogya AI", color = Color.White, fontSize = 24.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0C1117)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0C1117)) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToChat() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.Green
                        )
                    },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToSecond() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "Hospitals",
                            tint = Color.Green
                        )
                    },
                    label = { Text("Hospitals", color = Color.White) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already here */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.Green,
                        )
                    },
                    label = { Text("Info", color = Color.White) }
                )
            }
        },
        containerColor = Color(0xFF0C1117)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C1117))
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "External Links",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(6.dp)
            )

            Button(
                onClick = {
                    val url = "https://www.india.gov.in/national-health-portal"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(context, intent, null)  // ✅ open link
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "National Health Portal")
            }

            Button(
                onClick = {
                    val url = "https://www.mohfw.gov.in/?q=en"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(context, intent, null)  // ✅ open link
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ministry of Health")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun infoScreenPreview() {
    infoScreen(onNavigateToSecond = {}, onNavigateToChat = {})
}
