package com.groqsms.assistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

/**
 * Receives incoming SMS, forwards to Groq, and replies with the AI answer.
 * Heavy work is pushed onto a background thread so the broadcast finishes fast.
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        if (messages.isEmpty()) return

        val sender = messages.first().displayOriginatingAddress ?: return
        val body = messages.joinToString(separator = "") { it.displayMessageBody ?: "" }
        if (body.isBlank()) return

        if (Config.ALLOWED_SENDERS.isNotEmpty() && sender !in Config.ALLOWED_SENDERS) {
            Log.d(TAG, "Ignored SMS from $sender (not whitelisted)")
            return
        }

        val pendingResult = goAsync()
        Thread {
            try {
                handle(context.applicationContext, sender, body)
            } catch (t: Throwable) {
                Log.e(TAG, "handle() failed", t)
            } finally {
                pendingResult.finish()
            }
        }.start()
    }

    private fun handle(appContext: Context, sender: String, body: String) {
        Log.d(TAG, "From $sender: $body")

        ConversationStore.append(sender, "user", body)

        val groq = GroqClient()
        val messages = buildList {
            add(ConversationStore.Message("system", Config.SYSTEM_PROMPT))
            addAll(ConversationStore.snapshot(sender))
        }
        val reply = groq.chat(messages)?.let { truncate(it) }
            ?: "[error] could not reach Groq right now"

        ConversationStore.append(sender, "assistant", reply)
        SmsSender.send(sender, reply)
        Log.d(TAG, "Replied to $sender: $reply")
    }

    private fun truncate(text: String): String =
        if (text.length <= Config.MAX_REPLY_CHARS) text
        else text.substring(0, Config.MAX_REPLY_CHARS - 1) + "…"

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
