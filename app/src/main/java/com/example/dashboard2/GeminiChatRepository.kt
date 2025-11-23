package com.example.dashboard2

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiChatRepository {
    // ⚠️ PENTING: Ganti dengan API key Anda sendiri atau gunakan BuildConfig
    // Untuk production, simpan di local.properties:
    // GEMINI_API_KEY=your_api_key_here
    private const val API_KEY = "AIzaSyDQmfAR1MfAbA0QqIKmpX5yJE9t3kPIi-Y"

    suspend fun getResponse(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.5-pro",
            apiKey = API_KEY
        )

        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            return Chat(
                prompt = response.text ?: "Maaf, saya tidak dapat memproses permintaan Anda.",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            return Chat(
                prompt = "Error: ${e.message ?: "Terjadi kesalahan saat memproses permintaan."}",


                bitmap = null,
                isFromUser = false
            )
        }
    }

    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", // gemini-pro-vision sudah deprecated
            apiKey = API_KEY
        )

        try {
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            return Chat(
                prompt = response.text ?: "Maaf, saya tidak dapat menganalisis gambar ini.",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            return Chat(
                prompt = "Error: ${e.message ?: "Gagal menganalisis gambar."}",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}