package com.example.airalert

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val regions = listOf(
        "Вінницька область", "Волинська область", "Дніпропетровська область",
        "Донецька область", "Житомирська область", "Закарпатська область",
        "Запорізька область", "Івано-Франківська область", "Київська область",
        "Кіровоградська область", "Луганська область", "Львівська область",
        "Миколаївська область", "Одеська область", "Полтавська область",
        "Рівненська область", "Сумська область", "Тернопільська область",
        "Харківська область", "Херсонська область", "Хмельницька область",
        "Черкаська область", "Чернівецька область", "Чернігівська область",
        "м. Київ", "Севастополь"
    )
    private lateinit var spinner: Spinner

    private val perm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { startMonitoring() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item, regions)
            setSelection(regions.indexOf("Київська область"))
        }
        val start = Button(this).apply {
            text = "Запустити моніторинг"
            setOnClickListener { perm.launch(Manifest.permission.POST_NOTIFICATIONS) }
        }
        val stop = Button(this).apply {
            text = "Зупинити"
            setOnClickListener { stopService(Intent(this@MainActivity, AlertService::class.java)) }
        }
        val sources = Button(this).apply {
            text = "Мапа загроз"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, ThreatSourcesActivity::class.java))
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            addView(spinner); addView(start); addView(stop); addView(sources)
        })
    }

    private fun startMonitoring() {
        val region = spinner.selectedItem as String
        startForegroundService(
            Intent(this, AlertService::class.java).putExtra("region", region))
    }
}
