package com.farsheel.mypos.data.remote

import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.remote.request.EmailReceiptRequest
import com.farsheel.mypos.data.remote.request.LoginRequest
import com.farsheel.mypos.data.remote.request.OrderRequest
import com.farsheel.mypos.data.remote.response.*
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

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

    @Multipart
    @POST("api/product/upload_image")
    fun uploadProductImage(@Query("product_id") productId:String, @Part file: MultipartBody.Part): Single<ProductCreateResponse>
}