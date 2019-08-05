package com.farsheel.mypos.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "Category")
data class CategoryEntity(
    @SerializedName("cat_id") @PrimaryKey  @ColumnInfo(name = "CategoryId", index = true) var catId: Long?,
    @SerializedName("cat_name") @ColumnInfo(name = "CategoryName") val name: String,
    @SerializedName("cat_description") @ColumnInfo(name = "DescriptionDescription") val description: String
)
