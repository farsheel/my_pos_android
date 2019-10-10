package com.farsheel.mypos.view.qr

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.data.remote.response.OrderResponse
import com.farsheel.mypos.data.repository.CartRepository
import com.farsheel.mypos.data.repository.OrderRepository
import com.farsheel.mypos.util.Event
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class QrViewModel(
    cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    val amountToPay = cartRepository.cartVatTotalPay

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _lesserAmountEntered = MutableLiveData<Event<Boolean>>()
    val lesserAmountEntered: LiveData<Event<Boolean>> get() = _lesserAmountEntered

    val navigateToCompleted = MutableLiveData<Event<Double>>()

    val qrImage = MutableLiveData<Bitmap>()

    private val disposable = CompositeDisposable()

    val busy: ObservableField<Boolean> = ObservableField()



    fun textToImageEncode(value: String) {
        //withContext(Dispatchers.IO) {
        qrImage.postValue(orderRepository.generateQr(value))
        // }
    }

    fun getOrder(orderId: Long): LiveData<OrderDetailEntity> {
        return orderRepository.getOrderById(orderId)
    }

    fun subscribeForPaymentStatus(orderId: Long) {
        busy.set(true)
        orderRepository.fetchOrderFromRemote(orderId).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : SingleObserver<OrderResponse> {
                override fun onSuccess(t: OrderResponse) {
                    busy.set(false)
                    val orderDetailEntity = t.data?.paymentStatus
                    if (orderDetailEntity != null && orderDetailEntity.contentEquals("success")) {
                        orderRepository.updatePaymentStatus("success", orderId)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onError(e: Throwable) {
                    busy.set(false)
                }
            })
    }

    fun clearDisposables() {
        disposable.dispose()
        disposable.clear()

    }

    override fun onCleared() {
        super.onCleared()
        clearDisposables()
    }
}
