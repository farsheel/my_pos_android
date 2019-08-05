package com.farsheel.mypos.data.remote.response

import com.google.gson.annotations.SerializedName

data class GenericResponse(@SerializedName("message")
                           val message: String = "",
                           @SerializedName("status")
                           val status: Boolean = false)