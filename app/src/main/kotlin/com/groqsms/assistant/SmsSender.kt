package com.groqsms.assistant

import android.telephony.SmsManager

/**
 * Sends an SMS through the system SmsManager.
 */
object SmsSender {

    fun send(number: String, text: String) {
        val mgr = SmsManager.getDefault()
        val parts = mgr.divideMessage(text)
        if (parts.size > 1) {
            mgr.sendMultipartTextMessage(number, null, parts, null, null)
        } else {
            mgr.sendTextMessage(number, null, text, null, null)
        }
    }
}
