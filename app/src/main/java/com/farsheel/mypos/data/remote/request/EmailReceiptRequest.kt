package com.farsheel.mypos.data.remote.request

import com.google.gson.annotations.SerializedName

data class EmailReceiptRequest(
    @SerializedName("order_id")
    val orderId: Long? = 0,
    @SerializedName("email")
    val email: String? = ""
)