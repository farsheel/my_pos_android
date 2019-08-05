package com.farsheel.mypos.data.model

import androidx.room.*


@Entity(
    tableName = "Cart",
    foreignKeys = [ForeignKey(
        entity = ProductEntity::class,
        parentColumns = arrayOf("ItemId"),
        childColumns = arrayOf("ProductId"),
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("ProductId")]
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", index = true) var id: Long,
    @ColumnInfo(name = "ProductId") val productId: Long,
    @ColumnInfo(name = "ProductName") val name: String,
    @ColumnInfo(name = "Quantity") var quantity: Double,
    @ColumnInfo(name = "ProductPrice") var productPrice: Double
)