package com.farsheel.mypos.data.remote.response


import com.farsheel.mypos.data.model.OrderItemEntity
import com.google.gson.annotations.SerializedName


data class OrderCreateResponse(
    @SerializedName("item")
    val item: List<OrderItemEntity>?,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("order_id")
    val orderId: Long = 0,
    @SerializedName("status")
    val status: Boolean = false
)


