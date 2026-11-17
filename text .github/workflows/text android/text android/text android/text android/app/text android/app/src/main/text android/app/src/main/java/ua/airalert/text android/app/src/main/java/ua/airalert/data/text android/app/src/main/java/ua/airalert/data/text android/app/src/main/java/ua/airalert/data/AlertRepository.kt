kotlin
package ua.airalert.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlertRepository {

    private val api: UbillingApi =
        Retrofit.Builder()
            .baseUrl("https://ubilling.net.ua/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(UbillingApi::class.java)

    suspend fun getAlerts(): UbillingResponse {
        return api.getAlerts()
    }
}
