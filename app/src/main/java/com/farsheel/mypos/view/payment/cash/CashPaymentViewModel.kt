package com.farsheel.mypos.view.payment.cash

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.data.model.OrderItemEntity
import com.farsheel.mypos.data.remote.request.OrderRequest
import com.farsheel.mypos.data.remote.response.OrderCreateResponse
import com.farsheel.mypos.data.repository.CartRepository
import com.farsheel.mypos.data.repository.OrderRepository
import com.farsheel.mypos.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class CashPaymentViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    var orderId: Long = 0
    private var mDisposable: CompositeDisposable = CompositeDisposable()
    val amountEntered: ObservableField<String> = ObservableField()

    val amountToPay = cartRepository.cartVatTotalPay


    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _lesserAmountEntered = MutableLiveData<Event<Boolean>>()
    val lesserAmountEntered: LiveData<Event<Boolean>> get() = _lesserAmountEntered

    private val _navigateToCompleted = MutableLiveData<Event<Double>>()
    val navigateToCompleted: LiveData<Event<Double>> get() = _navigateToCompleted

    val busy: ObservableField<Boolean> = ObservableField()

    fun onSelectSuggestion(suggestion: Double) {
        amountEntered.set(suggestion.toString())
    }

    fun onClickContinue() {
        if (amountEntered.get()?.toDouble()!! < amountToPay.value!!) {
            _lesserAmountEntered.postValue(Event(true))
        } else {
            createOrder()
        }
    }

    private fun setBusy(isBusy: Boolean) {
        busy.set(isBusy)
    }

    private fun createOrder() {

        setBusy(true)

        val createList = cartRepository.cartSingleAll
            .map { cartEntityList ->
                val list = ArrayList<OrderItemEntity>()
                for (cartEntity in cartEntityList) {
                    list.add(
                        OrderItemEntity(
                            id = null,
                            orderId = null,
                            quantity = cartEntity.quantity,
                            name = cartEntity.name,
                            productPrice = cartEntity.productPrice,
                            productId = cartEntity.productId
                        )
                    )
                }
                return@map list
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                val orderRequest = OrderRequest(list)
                sendOrder(orderRequest)
            }
        mDisposable.add(createList)
    }

    private fun sendOrder(orderRequest: OrderRequest) {

        val createApi = orderRepository.createOrderRemote(orderRequest)
            .subscribeWith(object : DisposableSingleObserver<OrderCreateResponse>() {
                override fun onSuccess(response: OrderCreateResponse) {

                    if (response.status) {

                        saveOrderLocally(response)
                    } else {
                        setBusy(false)
                        _errorMessage.value = Event(response.message)
                    }
                }

                override fun onError(e: Throwable) {
                    setBusy(false)
                    _errorMessage.value = Event(e.localizedMessage)
                }
            })

        mDisposable.add(createApi)
    }

    private fun saveOrderLocally(response: OrderCreateResponse) {
        orderId = response.orderId
        val balance: Double? = amountToPay.value?.let { amountEntered.get()?.toDouble()?.minus(it) }
        val orderDetailEntity = OrderDetailEntity(
            orderId = response.orderId,
            customerId = 0,
            orderStatus = "completed",
            date = System.currentTimeMillis(),
            orderTotal = amountToPay.value.takeUnless { it == 00.00 }
                ?: amountToPay.value!!)

        val createOrderDetail = orderRepository.saveOrderLocal(orderDetailEntity)
            .subscribeWith(object : DisposableSingleObserver<Long>() {
                override fun onSuccess(t: Long) {
                    setBusy(false)
                    response.item?.let { list ->
                        orderRepository.saveOrderItems(list)
                            .subscribe { _, _ ->
                                cartRepository.clearCart()
                                _navigateToCompleted.postValue(Event(balance.takeUnless { it == 00.00 }
                                    ?: balance!!))
                            }
                    }
                }

                override fun onError(e: Throwable) {
                    setBusy(false)
                    _errorMessage.value = Event(
                        cartRepository.getResources().getString(
                            R.string.something_went_wrong
                        )
                    )
                }
            })
        mDisposable.add(createOrderDetail)
    }

    override fun onCleared() {
        super.onCleared()
        mDisposable.clear()
    }
}

