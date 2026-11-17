kotlin
package ua.airalert.data

import retrofit2.http.GET
import retrofit2.http.Query

interface UbillingApi {

    @GET("aerialalerts/")
    suspend fun getAlerts(
        @Query("json") json: Boolean = true,
        @Query("source") source: String = "default"
    (sad) UbillingResponse
}
