package com.farsheel.mypos.view.order

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.data.repository.OrderRepository

class OrderHistoryViewModel(private val orderRepository: OrderRepository) : BaseViewModel() {

    fun getOrderHistory(): LiveData<PagedList<OrderDetailEntity>> {
        return orderRepository.productList
    }
    fun searchOrder(search:String?){
        orderRepository.filterTextAll.postValue(search)
    }

}
