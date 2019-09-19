package com.farsheel.mypos.view.cart

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CartEntity
import com.farsheel.mypos.util.Event
import io.reactivex.schedulers.Schedulers

class CartViewModel(application: Application) : BaseViewModel(application) {

    @Bindable
    val selectedItem: MutableLiveData<CartEntity> = MutableLiveData()

    @Bindable
    val enteredQuantity: MutableLiveData<String> = MutableLiveData()
    @Bindable
    val enteredPrice: MutableLiveData<String> = MutableLiveData()
    val cartList = LivePagedListBuilder(
        AppDatabase.invoke(application).cartDao().getAllPaged(),
        20
    ).build()

    private val _itemEditApply = MutableLiveData<Event<Boolean>>()
    val itemEditApply: LiveData<Event<Boolean>> get() = _itemEditApply

    private val _onClickPay = MutableLiveData<Event<Boolean>>()
    val onClickPay: LiveData<Event<Boolean>> get() = _onClickPay

    private val _closeBottomSheet = MutableLiveData<Event<Boolean>>()
    val closeBottomSheet: LiveData<Event<Boolean>> get() = _closeBottomSheet

    @Bindable
    val cartSubTotal = AppDatabase.invoke(application).cartDao().getCartTotal()


    @Bindable
    val cartVAT = AppDatabase.invoke(application).cartDao().getCartVatTotal()


    @Bindable
    val discountApplied : MutableLiveData<Double> = MutableLiveData()


    fun addToCart(cartEntity: CartEntity) {
        AppDatabase.invoke(getApplication()).cartDao().insert(cartEntity)
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun removeCart(cartEntity: CartEntity) {
        AppDatabase.invoke(getApplication()).cartDao().deleteById(cartEntity.id)
            .subscribeOn(Schedulers.io()).subscribe()

    }



    fun onClickPay(){
        _onClickPay.postValue(Event(true))
    }

    fun closeBottomSheet(){
        _closeBottomSheet.postValue(Event(true))
    }

    fun updateItem() {
        if (enteredQuantity.value.isNullOrEmpty()){
            enteredQuantity.postValue("1")
        }
        if (enteredPrice.value.isNullOrEmpty()){
            enteredPrice.postValue("0")
        }
        _itemEditApply.postValue(Event(true))
    }
}
