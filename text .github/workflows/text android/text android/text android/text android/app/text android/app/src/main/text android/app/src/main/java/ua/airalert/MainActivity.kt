kotlin
package ua.airalert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.getSystemService
import ua.airalert.ui.MainScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContent {
            MainScreen()
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "air_alert",
                "Повітряна тривога",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Сповіщення про повітряні тривоги"

                enableVibration(true)
            }

            val manager =
                getSystemService<NotificationManager>()

            manager?.createNotificationChannel(channel)
        }
    }
}
