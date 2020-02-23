package com.farsheel.mypos.view.product.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.repository.ProductRepository
import com.farsheel.mypos.util.Event


class ProductListViewModel(productRepository: ProductRepository) : BaseViewModel() {

    private val _navigateToAddNew = MutableLiveData<Event<Boolean>>()
    val navigateToAddNew: LiveData<Event<Boolean>> get() = _navigateToAddNew
    var filterTextAll = MutableLiveData<String>()

    val productList: LiveData<PagedList<ProductEntity>> = productRepository.productList

    fun navigateToAdd() {
        _navigateToAddNew.value = Event(true)
    }
}
