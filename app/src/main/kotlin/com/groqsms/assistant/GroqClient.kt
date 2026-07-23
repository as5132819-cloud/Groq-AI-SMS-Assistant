package com.groqsms.assistant

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Thin Groq client. Uses the OpenAI-compatible chat completions endpoint.
 */
class GroqClient(private val apiKey: String = Config.GROQ_API_KEY) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    fun chat(messages: List<ConversationStore.Message>): String? {
        if (apiKey.isBlank() || apiKey == "REPLACE_WITH_YOUR_GROQ_API_KEY") {
            return "[config error] GROQ_API_KEY is not set"
        }

        val arr = JSONArray()
        for (m in messages) {
            arr.put(JSONObject().put("role", m.role).put("content", m.content))
        }

        val body = JSONObject()
            .put("model", Config.GROQ_MODEL)
            .put("messages", arr)
            .put("temperature", 0.7)
            .put("max_tokens", 256)
            .toString()
            .toRequestBody(jsonMedia)

        val request = Request.Builder()
            .url(Config.GROQ_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        return runCatching {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val raw = response.body?.string() ?: return null
                val json = JSONObject(raw)
                json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            }
        }.getOrNull()
    }
}
