package com.farsheel.mypos.view.payment.airtel

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.data.model.OrderItemEntity
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.request.OrderRequest
import com.farsheel.mypos.data.remote.response.OrderCreateResponse
import com.farsheel.mypos.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class AirtelPaymentViewModel(application: Application) : BaseViewModel(application) {

    var orderId: Long = 0
    private var mDisposable: CompositeDisposable = CompositeDisposable()
    @Bindable
    val amountEntered: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val numberEntered: MutableLiveData<String> = MutableLiveData()


//    @Bindable
//    val amountToPay = AppDatabase.invoke(application).cartDao().getCartTotal()

    @Bindable
    val amountToPay = AppDatabase.invoke(application).cartDao().getCartVatTotalPay()

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _lesserAmountEntered = MutableLiveData<Event<Boolean>>()
    val lesserAmountEntered: LiveData<Event<Boolean>> get() = _lesserAmountEntered

    private val _navigateToCompleted = MutableLiveData<Event<Double>>()
    val navigateToCompleted: LiveData<Event<Double>> get() = _navigateToCompleted

    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> get() = _busy

    fun onSelectSuggestion(suggestion: Double) {
        amountEntered.postValue(suggestion.toString())
        notifyPropertyChanged(BR.amountEntered)
    }

    fun onClickContinue() {
        if (amountEntered.value?.toDouble()!! < amountToPay.value!!) {
            _lesserAmountEntered.postValue(Event(true))
        } else {
            createOrder()
        }
    }

    private fun setBusy(isBusy: Boolean) {
        _busy.value = isBusy
        notifyChange()
    }

    private fun createOrder() {

        setBusy(true)

        val createList = AppDatabase.invoke(getApplication()).cartDao().getSingleAll()
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

        val createApi = ApiClient.getApiService(getApplication()).createOrder(orderRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        val balance: Double? = amountToPay.value?.let { amountEntered.value?.toDouble()?.minus(it) }
        val orderDetailEntity = OrderDetailEntity(
            orderId = response.orderId,
            customerId = 0,
            orderStatus = "completed",
            date = System.currentTimeMillis(),
            orderTotal = amountToPay.value.takeUnless { it == 00.00 }
                ?: amountToPay.value!!)

        val createOrderDetail = AppDatabase.invoke(getApplication()).orderDao()
            .insert(orderDetailEntity).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Long>() {
                override fun onSuccess(t: Long) {
                    setBusy(false)
                    response.item?.let { list ->
                        AppDatabase.invoke(getApplication()).orderDao()
                            .insertOrderItemList(list).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { _, _ ->
                                AppDatabase.invoke(getApplication()).cartDao()
                                    .deleteAll().subscribeOn(Schedulers.io())
                                    .subscribe()
                                _navigateToCompleted.postValue(Event(balance.takeUnless { it == 00.00 }
                                    ?: balance!!))
                            }
                    }
                }

                override fun onError(e: Throwable) {
                    setBusy(false)
                    _errorMessage.value = Event(
                        getApplication<Application>().getString(
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

