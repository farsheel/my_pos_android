package com.farsheel.mypos.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderDetail")
class OrderDetailEntity(
    @PrimaryKey @ColumnInfo(name = "OrderId", index = true) var orderId: Long,
    @ColumnInfo(name = "CustomerId") val customerId: Long,
    @ColumnInfo(name = "OrderTotal") val orderTotal: Double,
    @ColumnInfo(name = "OrderStatus") val orderStatus: String,
    @ColumnInfo(name = "OrderDate") val date: Long
)