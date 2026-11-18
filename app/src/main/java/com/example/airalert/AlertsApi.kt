package com.example.airalert

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlertsApi {
    private val client = OkHttpClient()
    private const val URL = "https://ubilling.net.ua/aerialalerts/"
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private const val MAX_AGE_MINUTES = 5L

    sealed class Result {
        data class Ok(val alert: Boolean, val changed: String) : Result()
        data class Error(val reason: String) : Result()
    }

    fun fetch(region: String): Result {
        try {
            val req = Request.Builder().url(URL).build()
            client.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return Result.Error("HTTP ${r.code}")
                val body = r.body?.string() ?: return Result.Error("порожня відповідь")
                val json = JSONObject(body)

                val cached = LocalDateTime.parse(json.getString("cachedat"), fmt)
                val now = LocalDateTime.now(ZoneId.of("Europe/Kyiv"))
                if (Duration.between(cached, now).abs().toMinutes() > MAX_AGE_MINUTES)
                    return Result.Error("дані застаріли")

                val state = json.getJSONObject("states").optJSONObject(region)
                    ?: return Result.Error("регіон не знайдено: $region")
                return Result.Ok(state.getBoolean("alertnow"), state.getString("changed"))
            }
        } catch (e: Exception) {
            return Result.Error(e.message ?: "помилка мережі")
        }
    }
}
