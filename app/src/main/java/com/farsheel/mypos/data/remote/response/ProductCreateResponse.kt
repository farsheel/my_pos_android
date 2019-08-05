package com.farsheel.mypos.data.remote.response


import com.farsheel.mypos.data.model.ProductEntity
import com.google.gson.annotations.SerializedName

data class ProductCreateResponse(
    @SerializedName("data")
    val data: ProductEntity,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("status")
    val status: Boolean = false
)




