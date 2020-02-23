package com.farsheel.mypos.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.data.model.OrderItemEntity
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.request.OrderRequest
import com.farsheel.mypos.data.remote.response.OrderCreateResponse
import com.farsheel.mypos.data.remote.response.OrderResponse
import com.farsheel.mypos.util.Util
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class OrderRepository(context: Context, private val appDatabase: AppDatabase) :
    BaseRepository(context) {

    var filterTextAll = MutableLiveData<String>()

    val productList: LiveData<PagedList<OrderDetailEntity>> =
        Transformations.switchMap(filterTextAll) {
            if (it.isNullOrEmpty()) {
                return@switchMap LivePagedListBuilder(
                    appDatabase.orderDao().getAllPaged(), 20
                ).build()
            } else {
                return@switchMap LivePagedListBuilder(
                    appDatabase.orderDao().getAllPaged(it), 20
                ).build()
            }
        }


    fun createOrderRemote(orderRequest: OrderRequest): Single<OrderCreateResponse> {
        return ApiClient.getApiService(context).createOrder(orderRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun saveOrderLocal(orderDetailEntity: OrderDetailEntity): Single<Long> {
        return appDatabase.orderDao()
            .insert(orderDetailEntity).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun saveOrderItems(orderItemEntities: List<OrderItemEntity>): Single<List<Long>> {
        return appDatabase.orderDao()
            .insertOrderItemList(orderItemEntities).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun generateQr(s: String): Bitmap? {
        return Util.generateQR(s)
    }

    fun getOrderById(id: Long): LiveData<OrderDetailEntity> {
        return appDatabase.orderDao().findById(id)
    }

    fun fetchOrderFromRemote(orderId: Long): Single<OrderResponse> {
        return ApiClient.getApiService(context).getOrderById(orderId)
            .subscribeOn(Schedulers.io())

    }

    fun updatePaymentStatus(paymentStatus: String, orderId: Long) {
        appDatabase.orderDao().updatePaymentStatus(paymentStatus, orderId)
    }
}