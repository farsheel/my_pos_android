package com.farsheel.mypos.data.remote

import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.remote.request.EmailReceiptRequest
import com.farsheel.mypos.data.remote.request.LoginRequest
import com.farsheel.mypos.data.remote.request.OrderRequest
import com.farsheel.mypos.data.remote.response.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WebServices {
    @POST("api/login")
    fun login(@Body loginRequest: LoginRequest): Single<LoginResponse>

    @GET("api/category")
    fun category(@Query("page") page: Int): Single<CategoryResponse>

    @POST("api/category")
    fun createOrUpdateCategory(@Body categoryEntity: CategoryEntity): Single<CategoryCreateResponse>

    @GET("api/product")
    fun product(@Query("page") page: Int): Single<ProductResponse>

    @POST("api/product")
    fun createOrUpdateProduct(@Body productEntity: ProductEntity): Single<ProductCreateResponse>

    @POST("api/order")
    fun createOrder(@Body orderRequest: OrderRequest): Single<OrderCreateResponse>

    @POST("api/send_email_receipt")
    fun sendEmailReceipt(@Body emailReceiptRequest: EmailReceiptRequest): Single<GenericResponse>
}