package com.farsheel.mypos.data.model

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "Product", foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = arrayOf("CategoryId"),
        childColumns = arrayOf("ItemCategory"),
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("ItemCategory")]
)
data class ProductEntity(
    @SerializedName("product_id") @PrimaryKey @ColumnInfo(name = "ItemId") var itemId: Long?,
    @SerializedName("product_name") @ColumnInfo(name = "ItemName") val name: String,
    @SerializedName("product_upc") @ColumnInfo(name = "ItemUPC") val upc: String,
    @SerializedName("product_price") @ColumnInfo(name = "ItemPrice") val price: Double,
    @SerializedName("product_cat_id") @ColumnInfo(name = "ItemCategory") var category: String = "1",
    @SerializedName("product_description") @ColumnInfo(name = "ItemDescription") val description: String
)
