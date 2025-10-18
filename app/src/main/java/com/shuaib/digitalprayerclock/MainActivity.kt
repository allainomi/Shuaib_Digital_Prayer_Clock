package com.shuaib.digitalprayerclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timeText: TextView
    private lateinit var dateText: TextView
    private lateinit var hijriText: TextView
    private lateinit var btnSetAzan: Button
    private lateinit var btnPlayAzan: Button
    private lateinit var btnToggleBackground: Button
    private lateinit var btnAbout: Button
    private lateinit var edtTime: EditText
    private var useKaabaBackground = true
    private var mediaPlayer: MediaPlayer? = null

    // Example online azan URL (replaceable)
    private val onlineAzanUrl = "https://server9.mp3quran.net/azaan/makkah.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeText = findViewById(R.id.textTime)
        dateText = findViewById(R.id.textDate)
        hijriText = findViewById(R.id.textHijri)
        btnSetAzan = findViewById(R.id.btnSetAzan)
        btnPlayAzan = findViewById(R.id.btnPlayAzan)
        btnToggleBackground = findViewById(R.id.btnToggleBackground)
        btnAbout = findViewById(R.id.btnAbout)
        edtTime = findViewById(R.id.editTime)

        updateTimeAndDate()

        btnSetAzan.setOnClickListener {
            val timeInput = edtTime.text.toString()
            if (timeInput.isNotEmpty()) {
                setAzanAlarm(timeInput)
                Toast.makeText(this, "آذان الارم سیٹ ہوگیا: $timeInput", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "براہ کرم وقت درج کریں (HH:mm)", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlayAzan.setOnClickListener {
            playOfflineAzan()
        }

        btnToggleBackground.setOnClickListener {
            useKaabaBackground = !useKaabaBackground
            val drawable = if (useKaabaBackground) R.drawable.kaaba_bg else R.drawable.madina_bg
            findViewById<ScrollView>(android.R.id.content).getChildAt(0).background = resources.getDrawable(drawable, null)
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun updateTimeAndDate() {
        val timer = Timer()
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val now = Date()
                    timeText.text = timeFormat.format(now)
                    dateText.text = dateFormat.format(now)
                    val cal = UmmalquraCalendar()
                    val hijriDate = "${cal.get(Calendar.DAY_OF_MONTH)} ${cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("ar"))} ${cal.get(Calendar.YEAR)} هـ"
                    hijriText.text = hijriDate
                }
            }
        }, 0, 1000)
    }

    private fun setAzanAlarm(time: String) {
        val parts = time.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AzanReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun playOfflineAzan() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.azan_offline)
            mediaPlayer?.start()
        } catch (e: Exception) {
            Toast.makeText(this, "آف لائن اذان چلانے میں مسئلہ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playOnlineAzan() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@MainActivity, Uri.parse(onlineAzanUrl))
                setOnPreparedListener { it.start() }
                prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Online Azan play میں مسئلہ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
