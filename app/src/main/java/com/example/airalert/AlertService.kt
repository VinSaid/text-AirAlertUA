package com.example.airalert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class AlertService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastAlert: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel("monitor", "Моніторинг", NotificationManager.IMPORTANCE_LOW))
        nm.createNotificationChannel(
            NotificationChannel("alert", "Тривога", NotificationManager.IMPORTANCE_HIGH))
        startForeground(1, NotificationCompat.Builder(this, "monitor")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Моніторинг тривог активний").build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val region = intent?.getStringExtra("region") ?: "Київська область"
        scope.launch {
            while (isActive) {
                when (val r = AlertsApi.fetch(region)) {
                    is AlertsApi.Result.Ok -> {
                        if (lastAlert != null && r.alert != lastAlert) notifyAlert(r.alert)
                        lastAlert = r.alert
                    }
                    is AlertsApi.Result.Error -> { /* TODO: показати "немає зв'язку" */ }
                }
                delay(20_000)
            }
        }
        return START_STICKY
    }

    private fun notifyAlert(alert: Boolean) {
        val (title, text) = if (alert)
            "🚨 ПОВІТРЯНА ТРИВОГА" to "Негайно в укриття!"
        else
            "✅ Відбій тривоги" to "Тривога в регіоні закінчилась"
        val n = NotificationCompat.Builder(this, "alert")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title).setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true).build()
        getSystemService(NotificationManager::class.java).notify(2, n)
    }

    override fun onDestroy() { scope.cancel(); super.onDestroy() }
    override fun onBind(i: Intent?): IBinder? = null
}
