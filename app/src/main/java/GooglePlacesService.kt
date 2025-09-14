package com.navdeep.aarogyaaitemp

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

object GooglePlacesService {
    private val client = OkHttpClient()

    fun getHospitals(city: String, apiKey: String): List<String> {
        val url = "https://maps.googleapis.com/maps/api/place/textsearch/json" +
                "?query=hospitals+in+$city" +
                "&key=$apiKey"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()

        val hospitals = mutableListOf<String>()
        val json = JSONObject(body)
        val results = json.getJSONArray("results")

        for (i in 0 until results.length()) {
            val obj = results.getJSONObject(i)
            val name = obj.optString("name")
            val address = obj.optString("formatted_address")
            hospitals.add("$name\n$address")
        }
        return hospitals
    }
}


