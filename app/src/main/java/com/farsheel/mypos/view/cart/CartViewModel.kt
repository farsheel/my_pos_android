package com.farsheel.mypos.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.CartEntity
import com.farsheel.mypos.data.repository.CartRepository
import com.farsheel.mypos.util.Event

class CartViewModel(private val cartRepository: CartRepository) : BaseViewModel() {

    val selectedItem: MutableLiveData<CartEntity> = MutableLiveData()

    val enteredQuantity: MutableLiveData<String> = MutableLiveData()
    val enteredPrice: MutableLiveData<String> = MutableLiveData()

    private val _itemEditApply = MutableLiveData<Event<Boolean>>()
    val itemEditApply: LiveData<Event<Boolean>> get() = _itemEditApply

    private val _onClickPay = MutableLiveData<Event<Boolean>>()
    val onClickPay: LiveData<Event<Boolean>> get() = _onClickPay

    private val _closeBottomSheet = MutableLiveData<Event<Boolean>>()
    val closeBottomSheet: LiveData<Event<Boolean>> get() = _closeBottomSheet

    val cartSubTotal = cartRepository.cartSubTotal


    val cartVAT = cartRepository.cartVatTotal

    val cartVATTotal = cartRepository.cartVatTotalPay

    val discountApplied: MutableLiveData<Double> = MutableLiveData()


    fun getSubTotal(): LiveData<Double> {
        return cartRepository.cartSubTotal
    }

    fun getCartList(): LiveData<PagedList<CartEntity>> {
        return cartRepository.cartList
    }

    fun addToCart(cartEntity: CartEntity) {
        cartRepository.addToCart(cartEntity)
    }

    fun removeCart(cartEntity: CartEntity) {
        cartRepository.removeCart(cartEntity)
    }


    fun onClickPay() {
        _onClickPay.postValue(Event(true))
    }

    fun closeBottomSheet() {
        _closeBottomSheet.postValue(Event(true))
    }

    fun updateItem() {
        if (enteredQuantity.value.isNullOrEmpty()) {
            enteredQuantity.postValue("1")
        }
        if (enteredPrice.value.isNullOrEmpty()) {
            enteredPrice.postValue("0")
        }
        _itemEditApply.postValue(Event(true))
    }
}
