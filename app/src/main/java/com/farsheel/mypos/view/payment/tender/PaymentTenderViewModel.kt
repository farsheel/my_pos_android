package com.farsheel.mypos.view.payment.tender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.repository.CartRepository
import com.farsheel.mypos.util.Event

class PaymentTenderViewModel(cartRepository: CartRepository) : BaseViewModel() {

    val amountToPay = cartRepository.cartVatTotalPay

    private val _onSelectCash = MutableLiveData<Event<Boolean>>()
    val onSelectCash: LiveData<Event<Boolean>> get() = _onSelectCash

    private val _onSelectCheque = MutableLiveData<Event<Boolean>>()
    val onSelectCheque: LiveData<Event<Boolean>> get() = _onSelectCheque


    fun onSelectCash() {
        _onSelectCash.postValue(Event(true))
    }

}
