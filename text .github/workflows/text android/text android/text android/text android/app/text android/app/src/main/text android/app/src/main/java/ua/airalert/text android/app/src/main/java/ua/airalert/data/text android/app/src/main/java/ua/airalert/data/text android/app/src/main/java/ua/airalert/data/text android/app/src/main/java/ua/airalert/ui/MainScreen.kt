```kotlin
package ua.airalert.ui

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.airalert.data.AlertRepository

private val Red = Color(0xFFD32F2F)
private val Green = Color(0xFF2E7D32)
private val Background = Color(0xFF101010)

@Composable
fun MainScreen() {

    val context = LocalContext.current

    val repository = remember {
        AlertRepository()
    }

    var isAlert by remember {
        mutableStateOf(false)
    }

    var source by remember {
        mutableStateOf("Ubilling")
    }

    var updated by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val region = "Київська область"

    var previousAlert by remember {
        mutableStateOf(false)
    }

    suspend fun loadAlerts() {

        loading = true
        error = null

        try {

            val response =
                repository.getAlerts()

            source =
                response.source ?: "Ubilling"

            updated =
                response.cachedAt ?: ""

            val current =
                response.states[region]?.alertNow == true

            isAlert = current

            /*              * Сповіщення тільки при зміні: *              * false -> true = початок тривоги              * true -> false = відбій */

            if (!previousAlert && current) {

                showAlertNotification(
                    context,
                    "🔴 ПОВІТРЯНА ТРИВОГА",
                    region
                )
            }

            if (previousAlert && !current) {

                showAlertNotification(
                    context,
                    "🟢 ВІДБІЙ ТРИВОГИ",
                    region
                )
            }

            previousAlert = current

        } catch (e: Exception) {

            error =
                e.message ?: "Помилка підключення"

        } finally {

            loading = false
        }
    }

    LaunchedEffect(Unit) {

        while (true) {

            loadAlerts()

            delay(10_000)
        }
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(16.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Top

    ) {

        Spacer(
            modifier =
                Modifier.height(40.dp)
        )

        Text(
            text = "AirAlert UA",
            style =
                MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(24.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (isAlert)
                            Red
                        else
                            Green
                )
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(30.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        if (isAlert)
                            "🔴 ПОВІТРЯНА ТРИВОГА"
                        else
                            "🟢 ЗАРАЗ СПОКІЙНО",

                    color = Color.White,

                    style =
                        MaterialTheme.typography.headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = region,
                    color = Color.White,
                    style =
                        MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Джерело",
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(source)

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Оновлено: ${
                            updated.ifBlank {
                                "очікування..."
                            }
                        }"
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        if (loading) {

            CircularProgressIndicator()

        } else {

            Button(
              onClick = {
                    // автоматичне оновлення працює
                }
            ) {

                Text("Оновлення автоматичне")
            }
        }

        if (error != null) {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(
                text =
                    "⚠️ ${error!!}",

                color =
                    Color(0xFFFFB74D)
            )
        }

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        Text(
            text =
                "Інформаційний сервіс. Під час реальної загрози орієнтуйтеся на офіційні сигнали цивільного захисту.",

            color =
                Color.LightGray,

            style =
                MaterialTheme.typography.bodySmall
        )
    }
}

private fun showAlertNotification(
    context: Context,
    title: String,
    text: String
) {

    val sound =
        RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_ALARM
        )

    val audioAttributes =
        AudioAttributes.Builder()
            .setUsage(
                AudioAttributes.USAGE_ALARM
            )
            .build()

    val notification =
        NotificationCompat.Builder(
            context,
            "air_alert"
        )
            .setSmallIcon(
                android.R.drawable.ic_dialog_alert
            )
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(
                NotificationCompat.PRIORITY_MAX
            )
            .setCategory(
                NotificationCompat.CATEGORY_ALARM
            )
            .setAutoCancel(true)
            .setSound(
                sound,
                audioAttributes
            )
            .setVibrate(
                longArrayOf(
                    0,
                    500,
                    300,
                    500,
                    300,
                    1000
                )
            )
            .build()

    val manager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    manager.notify(
        1001,
        notification
    )
}
