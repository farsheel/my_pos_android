package com.farsheel.mypos.view.category.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.repository.CategoryRepository
import com.farsheel.mypos.util.Event


class CategoryListViewModel(private val categoryRepository: CategoryRepository) : BaseViewModel() {

    private val _navigateToAddNew = MutableLiveData<Event<Boolean>>()
    val navigateToAddNew: LiveData<Event<Boolean>> get() = _navigateToAddNew

    fun navigateToAdd() {
        _navigateToAddNew.value = Event(true)
    }
    fun getCategoryList(): LiveData<PagedList<CategoryEntity>> {
        return categoryRepository.categoryList
    }
    fun updateSearch( search:String?) {
        categoryRepository.filterTextAll.postValue(search)
    }
}
