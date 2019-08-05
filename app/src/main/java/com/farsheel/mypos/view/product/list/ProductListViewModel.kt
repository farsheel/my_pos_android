package com.farsheel.mypos.view.product.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.util.Event


class ProductListViewModel(application: Application) : BaseViewModel(application) {

    private val _navigateToAddNew = MutableLiveData<Event<Boolean>>()
    val navigateToAddNew: LiveData<Event<Boolean>> get() = _navigateToAddNew
    var filterTextAll = MutableLiveData<String>()

    val productList: LiveData<PagedList<ProductEntity>> = Transformations.switchMap(filterTextAll) {
        if (it.isNullOrEmpty()) {
            return@switchMap LivePagedListBuilder(
                AppDatabase.invoke(getApplication()).productDao().getAll(), 20
            ).build()
        } else {
            return@switchMap LivePagedListBuilder(
                AppDatabase.invoke(getApplication()).productDao().getAllByQuery(it), 20
            ).build()
        }
    }

    fun navigateToAdd() {
        _navigateToAddNew.value = Event(true)
    }
}
