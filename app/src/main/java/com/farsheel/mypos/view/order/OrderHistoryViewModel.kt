package com.farsheel.mypos.view.order

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.OrderDetailEntity

class OrderHistoryViewModel(application: Application) : BaseViewModel(application) {

    var filterTextAll = MutableLiveData<String>()

    val productList: LiveData<PagedList<OrderDetailEntity>> =
        Transformations.switchMap(filterTextAll) {
            if (it.isNullOrEmpty()) {
                return@switchMap LivePagedListBuilder(
                    AppDatabase.invoke(getApplication()).orderDao().getAllPaged(), 20
                ).build()
            } else {
                return@switchMap LivePagedListBuilder(
                    AppDatabase.invoke(getApplication()).orderDao().getAllPaged(it), 20
                ).build()
            }
        }
}
