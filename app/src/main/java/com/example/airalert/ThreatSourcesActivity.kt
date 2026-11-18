package com.example.airalert

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThreatSourcesActivity : AppCompatActivity() {

    companion object {
        const val NEPTUN_URL = "https://neptun.in.ua"
        // Вставте перевірене посилання на Telegram-канал «Небосхил», наприклад "https://t.me/назва"
        const val NEBOSKHYL_TG = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val web = WebView(this).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(NEPTUN_URL)
        }
        val neptun = Button(this).apply {
            text = "Нептун (мапа)"; setOnClickListener { web.loadUrl(NEPTUN_URL) }
        }
        val nebo = Button(this).apply {
            text = "Небосхил (Telegram)"
            setOnClickListener {
                if (NEBOSKHYL_TG.isBlank())
                    Toast.makeText(this@ThreatSourcesActivity,
                        "Посилання на канал ще не вказано", Toast.LENGTH_LONG).show()
                else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NEBOSKHYL_TG)))
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(neptun); addView(nebo)
            addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        })
    }
}
