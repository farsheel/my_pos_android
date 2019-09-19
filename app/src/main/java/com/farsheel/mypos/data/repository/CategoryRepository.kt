package com.farsheel.mypos.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.response.CategoryCreateResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CategoryRepository(context: Context, val appDatabase: AppDatabase) :
    BaseRepository(context) {

    var filterTextAll = MutableLiveData<String>()
    val categoryList: LiveData<PagedList<CategoryEntity>> =
        Transformations.switchMap(filterTextAll) {
            if (it.isNullOrEmpty()) {
                return@switchMap LivePagedListBuilder(
                    appDatabase.categoryDao().getAll(), 20
                ).build()
            } else {
                return@switchMap LivePagedListBuilder(
                    appDatabase.categoryDao().getAllByQuery(it), 20
                ).build()
            }
        }

    fun getPagedCategories(): LiveData<PagedList<CategoryEntity>> {
        return categoryList
    }
    fun getCategories(): LiveData<List<CategoryEntity>> {
        return appDatabase.categoryDao().getAllList()
    }

    fun addEditCategoryRemote(categoryEntity: CategoryEntity): Single<CategoryCreateResponse> {
        return ApiClient.getApiService(context).createOrUpdateCategory(categoryEntity)
            .subscribeOn(Schedulers.io())
    }


    fun saveCategory(categoryEntity: CategoryEntity): Single<Long> {
        return AppDatabase.invoke(context).categoryDao().insert(categoryEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }
}