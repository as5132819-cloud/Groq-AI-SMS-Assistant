package com.groqsms.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Minimal launcher screen. Its only job is to request SMS permissions
 * and show a status line. The real work happens in SmsReceiver.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val status = findViewById<TextView>(R.id.status)
        status.text = when {
            hasSmsPermissions() ->
                "Ready. Send an SMS to this device to chat with the AI."
            else -> "Requesting SMS permissions..."
        }

        if (!hasSmsPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS
                ),
                REQ_CODE
            )
        }
    }

    private fun hasSmsPermissions(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQ_CODE = 1001
    }
}
