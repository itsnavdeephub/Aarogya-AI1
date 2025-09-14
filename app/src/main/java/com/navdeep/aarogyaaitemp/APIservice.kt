package com.navdeep.aarogyaaitemp

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call

// Request models for Gemini
data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

// Response models for Gemini
data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?,
    val finishReason: String?
)

interface GeminiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    fun generateContent(
        @Query("key") apiKey: String,   // ✅ Correct way
        @Body request: GeminiRequest
    ): Call<GeminiResponse>
}
