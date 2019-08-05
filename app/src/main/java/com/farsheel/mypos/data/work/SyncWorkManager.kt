package com.farsheel.mypos.data.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.response.CategoryResponse
import com.farsheel.mypos.data.remote.response.ProductResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver

class SyncWorkManager(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val disposables = CompositeDisposable()

    override fun doWork(): Result {

        try {
            Thread.sleep(200)
        } catch (e: Exception) {
        }

        syncCategory(1)
        syncProduct(1)

        disposables.clear()
        return Result.success()
    }

    private fun syncProduct(page: Int) {

        val productDispose = ApiClient.getApiService(applicationContext).product(page)
            .subscribeWith(object : DisposableSingleObserver<ProductResponse>() {
                override fun onSuccess(t: ProductResponse) {
                    t.data?.let {
                        AppDatabase.invoke(applicationContext).productDao().insertAll(it)
                    }
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                    }
                    if (t.lastPage != page) {
                        syncProduct((page + 1))
                    }

                }

                override fun onError(e: Throwable) {
                }

            })
        disposables.add(productDispose)
    }

    private fun syncCategory(page: Int) {
        val categoryDispose = ApiClient.getApiService(applicationContext).category(page)
            .subscribeWith(object : DisposableSingleObserver<CategoryResponse>() {
                override fun onSuccess(t: CategoryResponse) {
                    t.data?.let {
                        AppDatabase.invoke(applicationContext).categoryDao().insertAll(it)
                    }
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                    }
                    if (t.lastPage != page) {
                        syncCategory((page + 1))
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
        disposables.add(categoryDispose)
    }
}