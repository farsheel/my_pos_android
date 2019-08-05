package com.farsheel.mypos.view.payment.completed

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.FeedbackMessage
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.request.EmailReceiptRequest
import com.farsheel.mypos.data.remote.response.GenericResponse
import com.farsheel.mypos.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class PaymentCompletedViewModel(application: Application) : BaseViewModel(application) {

    private val compositeDisposable = CompositeDisposable()

    @Bindable
    var amountPaid: MutableLiveData<Double> = MutableLiveData()

    @Bindable
    var balance: MutableLiveData<Double> = MutableLiveData()

    @Bindable
    var orderId: MutableLiveData<Long> = MutableLiveData()

    private val _startNewSale = MutableLiveData<Event<Boolean>>()
    val startNewSale: LiveData<Event<Boolean>> get() = _startNewSale

    private val _onReceipt = MutableLiveData<Event<Boolean>>()
    val onReceipt: LiveData<Event<Boolean>> get() = _onReceipt

    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> get() = _busy

    val email: MutableLiveData<String> = MutableLiveData()

    private val _snackbarMessage = MutableLiveData<Event<FeedbackMessage>>()
    val snackbarMessage: LiveData<Event<FeedbackMessage>> get() = _snackbarMessage

    fun onNewSale() {
        _startNewSale.value = Event(true)
    }

    fun printReceipt() {
        setBusy(true)
        val emailReceiptRequest = EmailReceiptRequest(orderId.value, email.value)

        val loginDisposable =
            ApiClient.getApiService(getApplication()).sendEmailReceipt(emailReceiptRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GenericResponse>() {
                    override fun onSuccess(t: GenericResponse) {
                        val feedbackMessage: FeedbackMessage = if (t.status) {
                            FeedbackMessage(
                                color = ContextCompat.getColor(getApplication(), R.color.greenUi),
                                message = t.message
                            )
                        } else {
                            FeedbackMessage(
                                color = ContextCompat.getColor(getApplication(), R.color.redUi),
                                message = t.message
                            )
                        }
                        _snackbarMessage.value = Event(feedbackMessage)
                        setBusy(false)
                    }

                    override fun onError(e: Throwable) {
                        setBusy(false)
                        val feedbackMessage = FeedbackMessage(
                            color = ContextCompat.getColor(getApplication(), R.color.redUi),
                            message = getApplication<Application>().getString(R.string.something_went_wrong)
                        )
                        _snackbarMessage.value = Event(feedbackMessage)
                    }
                })

        compositeDisposable.add(loginDisposable)
    }

    private fun setBusy(isBusy: Boolean) {
        _busy.value = isBusy
        notifyChange()
    }
}
