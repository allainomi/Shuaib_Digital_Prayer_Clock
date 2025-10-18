package com.shuaib.digitalprayerclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer

class AzanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val player = MediaPlayer.create(context, R.raw.azan_offline)
            player?.start()
        } catch (e: Exception) {
            // ignore
        }
    }
}
