package com.farsheel.mypos.data.remote.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("access_token")
    val accessToken: String = "",
    @SerializedName("expires_at")
    val expiresAt: String = "",
    @SerializedName("data")
    val data: Data,
    @SerializedName("token_type")
    val tokenType: String = "",
    @SerializedName("message")
    val message: String = ""
)

data class Data(
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("email")
    val email: String = ""
)


