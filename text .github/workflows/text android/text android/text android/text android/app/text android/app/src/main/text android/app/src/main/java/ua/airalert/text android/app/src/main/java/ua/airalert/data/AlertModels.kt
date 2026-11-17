kotlin
package ua.airalert.data

import com.google.gson.annotations.SerializedName

data class UbillingResponse(

    @SerializedName("source")
    val source: String? = null,

    @SerializedName("cachedat")
    val cachedAt: String? = null,

    @SerializedName("states")
    val states: Map<String, AlertState> = emptyMap()
)

data class AlertState(

    @SerializedName("alertnow")
    val alertNow: Boolean = false,

    @SerializedName("changed")
    val changed: String? = null
)
