package com.farsheel.mypos.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "OrderDetail")
data class OrderDetailEntity(
    @SerializedName("order_id") @PrimaryKey @ColumnInfo(
        name = "OrderId",
        index = true
    ) var orderId: Long,
    @SerializedName("customer_id") @ColumnInfo(name = "CustomerId") val customerId: Long?,
    @ColumnInfo(name = "OrderTotal") val orderTotal: Double,
    @SerializedName("order_pay_id") @ColumnInfo(name = "PayModeId") val payModeId: Long = 1,
    @SerializedName("payment_status") @ColumnInfo(name = "PaymentStatus") val paymentStatus: String,
    @ColumnInfo(name = "OrderDate") val date: Long
)