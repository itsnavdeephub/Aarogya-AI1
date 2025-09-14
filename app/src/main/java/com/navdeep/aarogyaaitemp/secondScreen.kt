package com.navdeep.aarogyaaitemp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

// ✅ Load states & districts from JSON in assets
suspend fun loadStatesAndDistricts(context: Context): Map<String, List<String>> {
    return withContext(Dispatchers.IO) {
        val json = context.assets.open("list.json")
            .bufferedReader().use { it.readText() }
        val obj = JSONObject(json)
        val statesArray = obj.getJSONArray("states")

        val map = mutableMapOf<String, List<String>>()
        for (i in 0 until statesArray.length()) {
            val stateObj = statesArray.getJSONObject(i)
            val stateName = stateObj.getString("state")
            val districtsArray = stateObj.getJSONArray("districts")
            val districts = List(districtsArray.length()) { j -> districtsArray.getString(j) }
            map[stateName] = districts
        }
        map
    }
}


// ✅ Fetch hospitals from Google Places API
suspend fun fetchHospitalsFromGoogle(city: String, apiKey: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url = "https://maps.googleapis.com/maps/api/place/textsearch/json" +
                    "?query=hospitals+in+$city" +
                    "&key=$apiKey"

            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val hospitals = mutableListOf<String>()
            val json = JSONObject(body)
            val results = json.getJSONArray("results")

            for (i in 0 until results.length()) {
                val obj = results.getJSONObject(i)
                val name = obj.optString("name")
                val address = obj.optString("formatted_address")
                hospitals.add("$name\n$address")
            }
            hospitals
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(onNavigateToChat: () -> Unit, onNavigateToInfo: () -> Unit) {
    var selectedState by remember { mutableStateOf<String?>(null) }
    var selectedDistrict by remember { mutableStateOf<String?>(null) }

    var stateExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }

    var searchClicked by remember { mutableStateOf(false) }
    var hospitals by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val apiKey = "AIzaSyC9CynZT5GS3WkNpCL7x_1QlbfiZzr_4pM" // 🔑 replace with your real Google API key

    // ✅ States & districts from JSON
    var statesMap by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        statesMap = loadStatesAndDistricts(context)
    }

    val states = statesMap.keys.toList()
    val districts = selectedState?.let { statesMap[it] } ?: emptyList()

    Scaffold(
        containerColor = Color(0xFF0C1117),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0C1117)) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToChat() },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = Color.Green) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(imageVector = Icons.Default.LocalHospital, contentDescription = "Hospitals", tint = Color.Green) },
                    label = { Text("Hospitals", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToInfo() },
                    icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = Color.Green) },
                    label = { Text("Info", color = Color.White) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C1117))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Aarogya AI", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(16.dp))
            Text("Find Your Nearest Hospital", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(16.dp))

            // ✅ State dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { stateExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = selectedState ?: "Select State")
                }
                DropdownMenu(
                    expanded = stateExpanded,
                    onDismissRequest = { stateExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    states.forEach { state ->
                        DropdownMenuItem(
                            text = { Text(state) },
                            onClick = {
                                selectedState = state
                                selectedDistrict = null
                                stateExpanded = false
                                searchClicked = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ District dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { districtExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedState != null
                ) {
                    Text(text = selectedDistrict ?: "Select District")
                }
                DropdownMenu(
                    expanded = districtExpanded,
                    onDismissRequest = { districtExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    districts.forEach { district ->
                        DropdownMenuItem(
                            text = { Text(district) },
                            onClick = {
                                selectedDistrict = district
                                districtExpanded = false
                                searchClicked = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Search button
            Button(
                onClick = {
                    searchClicked = true
                    loading = true
                    coroutineScope.launch {
                        hospitals = fetchHospitalsFromGoogle(selectedDistrict!!, apiKey)
                        loading = false
                    }
                },
                enabled = selectedState != null && selectedDistrict != null
            ) {
                Text("Find Hospitals")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Results
            when {
                loading -> Text("Finding hospitals...", color = Color.White)
                searchClicked && hospitals.isEmpty() -> Text("No hospitals found", color = Color.White)
                hospitals.isNotEmpty() -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    hospitals.forEach { hospital ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(text = hospital, color = Color.White, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val gmmIntentUri = Uri.parse("geo:0,0?q=$hospital")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        context.startActivity(mapIntent)
                                    }
                                ) {
                                    Text("Open in Maps")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen(onNavigateToChat = {}, onNavigateToInfo = {})
}
