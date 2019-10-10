package com.farsheel.mypos.data.remote.response


import com.farsheel.mypos.data.model.OrderDetailEntity
import com.google.gson.annotations.SerializedName




data class OrderResponse(@SerializedName("data")
                         val data: OrderDetailEntity?,
                         @SerializedName("message")
                         val message: String = "",
                         @SerializedName("status")
                         val status: Boolean = false)


