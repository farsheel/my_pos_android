package com.farsheel.mypos.data.remote.response


import com.farsheel.mypos.data.model.CategoryEntity
import com.google.gson.annotations.SerializedName


data class CategoryCreateResponse(
    @SerializedName("data")
    val data: CategoryEntity,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("status")
    val status: Boolean = false
)


