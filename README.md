# Groq AI SMS Assistant

Android application that works as a personal AI assistant. The user sends a normal SMS to the device, the app forwards it to **Groq API** (model: `openai/gpt-oss-120b`), and the AI-generated reply is sent back as an SMS.

> For personal use. Priority: stable working prototype, not advanced UI.

---

## How it works

```
User sends SMS
   ↓
Android app receives SMS (BroadcastReceiver)
   ↓
App calls Groq API (openai/gpt-oss-120b)
   ↓
AI generates a reply
   ↓
App sends the reply back through SMS
```

---

## Tech stack

- **Platform:** Android (min SDK 24)
- **Language:** Kotlin
- **Build:** Gradle (Kotlin DSL)
- **CI:** GitHub Actions (no Android Studio required)
- **AI:** Groq API — model `openai/gpt-oss-120b`
- **HTTP:** OkHttp + OkIO
- **JSON:** org.json (built-in)

---

## Project structure

```
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/groqsms/assistant/
│       │   ├── MainActivity.kt
│       │   ├── SmsReceiver.kt
│       │   ├── SmsSender.kt
│       │   ├── GroqClient.kt
│       │   ├── ConversationStore.kt
│       │   └── Config.kt
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/strings.xml
├── .github/workflows/build.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## Setup

1. **Get a Groq API key** at https://console.groq.com
2. **Add your key** to `app/src/main/kotlin/com/groqsms/assistant/Config.kt` (or set the `GROQ_API_KEY` env var before building)
3. **Build the APK**
   - Locally: `./gradlew assembleDebug` (requires JDK 17+)
   - Or push to `main` — GitHub Actions builds the APK and uploads it as an artifact
4. **Install on your phone** — grant SMS permissions when prompted

---

## Notes

- Conversation history is in-memory only (per sender). Resets when the process dies.
- Replies are capped at ~300 characters to fit SMS limits.
- You can whitelist specific senders in `Config.kt`.
- The first priority is a stable working prototype, not advanced UI.
