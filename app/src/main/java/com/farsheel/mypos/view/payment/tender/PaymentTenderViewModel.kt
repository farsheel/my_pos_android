package com.farsheel.mypos.view.payment.tender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.repository.CartRepository
import com.farsheel.mypos.util.Event

class PaymentTenderViewModel(val cartRepository: CartRepository) : BaseViewModel() {

    val amountToPay = cartRepository.cartVatTotalPay

    private val _onSelectCash = MutableLiveData<Event<Boolean>>()
    val onSelectCash: LiveData<Event<Boolean>> get() = _onSelectCash

    private val _onSelectCheque = MutableLiveData<Event<Boolean>>()
    val onSelectCheque: LiveData<Event<Boolean>> get() = _onSelectCheque

    private val _onSelectAirtel = MutableLiveData<Event<Boolean>>()
    val onSelectAirtel: LiveData<Event<Boolean>> get() = _onSelectAirtel

    private val _onSelectMtn = MutableLiveData<Event<Boolean>>()
    val onSelectMtn: LiveData<Event<Boolean>> get() = _onSelectMtn

    private val _onSelectVodafone = MutableLiveData<Event<Boolean>>()
    val onSelectVodafone: LiveData<Event<Boolean>> get() = _onSelectVodafone

    private val _onSelectGmoney = MutableLiveData<Event<Boolean>>()
    val onSelectGmoney: LiveData<Event<Boolean>> get() = _onSelectGmoney

    private val _onSelectVisa = MutableLiveData<Event<Boolean>>()
    val onSelectVisa: LiveData<Event<Boolean>> get() = _onSelectVisa

    private val _onSelectMasterpass = MutableLiveData<Event<Boolean>>()
    val onSelectMasterpass: LiveData<Event<Boolean>> get() = _onSelectMasterpass


    fun onSelectCash() {
        _onSelectCash.postValue(Event(true))
    }

    fun onSelectAirtel() {
        _onSelectAirtel.postValue(Event(true))
    }


    fun onSelectMtn() {
        _onSelectMtn.postValue(Event(true))
    }

    fun onSelectVodafone() {
        _onSelectVodafone.postValue(Event(true))
    }

    fun onSelectGmoney() {
        _onSelectGmoney.postValue(Event(true))
    }


    fun onSelectVisa() {
        _onSelectVisa.postValue(Event(true))
    }

    fun onSelectMasterpass() {
        _onSelectMasterpass.postValue(Event(true))
    }
}
