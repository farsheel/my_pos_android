package com.farsheel.mypos.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.work.*
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.response.ProductCreateResponse
import com.farsheel.mypos.data.work.ProductImageUploadWork
import com.farsheel.mypos.data.work.SyncWorkManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProductRepository(val application: Application, val appDatabase: AppDatabase) :
    BaseRepository(application) {

    val selectedCat: MutableLiveData<Long> = MutableLiveData()

    val productList: LiveData<PagedList<ProductEntity>> = Transformations.switchMap(selectedCat) {
        if (it == null) {
            return@switchMap LivePagedListBuilder(
                appDatabase.productDao().getAll(),
                20
            ).build()

        } else {
            return@switchMap LivePagedListBuilder(
                appDatabase.productDao().getAllByCatId(selectedCat.value),
                20
            ).build()
        }
    }


    fun addEditProductRemote(productEntity: ProductEntity): Single<ProductCreateResponse> {

        return ApiClient.getApiService(application).createOrUpdateProduct(productEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadProductImage(itemId: Long?, path: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequestBuilder<ProductImageUploadWork>()
        work.addTag(SyncWorkManager::class.java.simpleName)
        work.setConstraints(constraints)
        val data = Data.Builder()
        //Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString("file_path", path)
        data.putString("product_id", itemId.toString())
        //Set Input Data
        work.setInputData(data.build())
        WorkManager.getInstance(application).enqueue(work.build())
    }

    fun saveProductLocal(productEntity: ProductEntity): Single<Long> {
        return appDatabase.productDao().insert(productEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}