package com.farsheel.mypos.data.repository

import android.content.Context
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.request.EmailReceiptRequest
import com.farsheel.mypos.data.remote.response.GenericResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ReceiptRepository(context: Context, private val appDatabase: AppDatabase) :
    BaseRepository(context) {

    fun emailReceipt(emailReceiptRequest: EmailReceiptRequest): Single<GenericResponse> {

        return ApiClient.getApiService(context).sendEmailReceipt(emailReceiptRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}