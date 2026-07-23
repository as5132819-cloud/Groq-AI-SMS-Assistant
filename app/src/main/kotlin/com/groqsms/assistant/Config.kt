package com.groqsms.assistant

/**
 * Central configuration. Keep it small and obvious.
 *
 * IMPORTANT: do NOT commit a real API key. For local builds, set GROQ_API_KEY
 * in your environment, or replace the value below for quick testing only.
 */
object Config {

    val GROQ_API_KEY: String =
        System.getenv("GROQ_API_KEY") ?: "REPLACE_WITH_YOUR_GROQ_API_KEY"

    const val GROQ_MODEL: String = "openai/gpt-oss-120b"

    const val GROQ_URL: String = "https://api.groq.com/openai/v1/chat/completions"

    const val SYSTEM_PROMPT: String =
        "You are a concise personal AI assistant replying via SMS. " +
        "Keep replies under 300 characters, friendly, and useful."

    const val MAX_REPLY_CHARS: Int = 300

    val ALLOWED_SENDERS: Set<String> = emptySet()
}
