package com.farsheel.mypos.data.remote.request


import com.farsheel.mypos.data.model.OrderItemEntity
import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("items")
    val items: List<OrderItemEntity>?,
    @SerializedName("order_pay_id")
    var payModeId: Long = 1
)


