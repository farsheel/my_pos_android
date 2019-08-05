package com.farsheel.mypos.view.category.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.util.Event


class CategoryListViewModel(application: Application) : BaseViewModel(application) {

    private val _navigateToAddNew = MutableLiveData<Event<Boolean>>()
    val navigateToAddNew: LiveData<Event<Boolean>> get() = _navigateToAddNew
    var filterTextAll = MutableLiveData<String>()

    val categoryList: LiveData<PagedList<CategoryEntity>> = Transformations.switchMap(filterTextAll) {
        if (it.isNullOrEmpty()) {
            return@switchMap LivePagedListBuilder(
                AppDatabase.invoke(getApplication()).categoryDao().getAll(), 20
            ).build()
        } else {
            return@switchMap LivePagedListBuilder(
                AppDatabase.invoke(getApplication()).categoryDao().getAllByQuery(it), 20
            ).build()
        }
    }

    fun navigateToAdd() {
        _navigateToAddNew.value = Event(true)
    }
}
