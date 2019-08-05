package com.farsheel.mypos.data.model

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "OrderItem",
    foreignKeys = [ForeignKey(
        entity = ProductEntity::class,
        parentColumns = arrayOf("ItemId"),
        childColumns = arrayOf("ProductId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = OrderDetailEntity::class,
        parentColumns = arrayOf("OrderId"),
        childColumns = arrayOf("OrderId")
    )], indices = [Index("ProductId")]
)
data class OrderItemEntity(
    @SerializedName("order_item_id") @PrimaryKey @ColumnInfo(
        name = "OrderItemId",
        index = true
    ) var id: Long?,
    @SerializedName("order_product_id") @ColumnInfo(name = "ProductId") val productId: Long,
    @SerializedName("order_id") @ColumnInfo(name = "OrderId", index = true) val orderId: Long?,
    @SerializedName("order_product_name") @ColumnInfo(name = "ProductName") val name: String,
    @SerializedName("quantity") @ColumnInfo(name = "Quantity") var quantity: Double,
    @SerializedName("order_price") @ColumnInfo(name = "ProductPrice") var productPrice: Double
)
