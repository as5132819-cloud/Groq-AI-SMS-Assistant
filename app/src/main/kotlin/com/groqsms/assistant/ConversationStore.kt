package com.groqsms.assistant

/**
 * Minimal in-memory conversation history keyed by sender phone number.
 * Resets when the process is killed — that is fine for a personal prototype.
 */
object ConversationStore {

    data class Message(val role: String, val content: String)

    private val history: MutableMap<String, MutableList<Message>> = HashMap()

    @Synchronized
    fun append(sender: String, role: String, content: String) {
        val list = history.getOrPut(sender) { mutableListOf() }
        list.add(Message(role, content))
        if (list.size > MAX_TURNS * 2) {
            while (list.size > MAX_TURNS * 2) list.removeAt(0)
        }
    }

    @Synchronized
    fun snapshot(sender: String): List<Message> =
        history[sender]?.toList() ?: emptyList()

    @Synchronized
    fun clear(sender: String) {
        history[sender]?.clear()
    }

    private const val MAX_TURNS = 10
}
