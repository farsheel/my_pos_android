package com.farsheel.mypos.view.payment.tender

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.util.Event

class PaymentTenderViewModel(application: Application) : BaseViewModel(application) {

    @Bindable
    val amountToPay = AppDatabase.invoke(application).cartDao().getCartTotal()


    private val _onSelectCash = MutableLiveData<Event<Boolean>>()
    val onSelectCash: LiveData<Event<Boolean>> get() = _onSelectCash

    private val _onSelectCheque = MutableLiveData<Event<Boolean>>()
    val onSelectCheque: LiveData<Event<Boolean>> get() = _onSelectCheque





    fun onSelectCash() {
        _onSelectCash.postValue(Event(true))
    }

    fun onSelectCheque() {
        _onSelectCheque.postValue(Event(true))
    }
}
