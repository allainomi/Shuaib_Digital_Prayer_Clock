package com.shuaib.digitalprayerclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val aboutText: TextView = findViewById(R.id.aboutText)
        aboutText.text = """
            Shuaib Digital Prayer Clock
                        
            یہ ایپ نماز کے اوقات، ہجری و شمسی تاریخ، اور ازان الارم کے لئے تیار کی گئی ہے.
                        
            By: مفتی شعیب آلائی
        """.trimIndent()
    }
}
