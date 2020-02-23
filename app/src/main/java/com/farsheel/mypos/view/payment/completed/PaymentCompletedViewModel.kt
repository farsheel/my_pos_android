package com.farsheel.mypos.view.payment.completed

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.FeedbackMessage
import com.farsheel.mypos.data.remote.request.EmailReceiptRequest
import com.farsheel.mypos.data.remote.response.GenericResponse
import com.farsheel.mypos.data.repository.ReceiptRepository
import com.farsheel.mypos.util.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver


class PaymentCompletedViewModel(private val receiptRepository: ReceiptRepository) :
    BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()

    var amountPaid: ObservableField<Double> = ObservableField()

    var balance: ObservableField<Double> = ObservableField()

    var orderId: ObservableField<Long> = ObservableField()

    private val _startNewSale = MutableLiveData<Event<Boolean>>()
    val startNewSale: LiveData<Event<Boolean>> get() = _startNewSale

    private val _onReceipt = MutableLiveData<Event<Boolean>>()
    val onReceipt: LiveData<Event<Boolean>> get() = _onReceipt

    val busy: ObservableField<Boolean> = ObservableField()

    val email: ObservableField<String> = ObservableField()

    private val _snackbarMessage = MutableLiveData<Event<FeedbackMessage>>()
    val snackbarMessage: LiveData<Event<FeedbackMessage>> get() = _snackbarMessage

    fun onNewSale() {
        _startNewSale.value = Event(true)
    }

    fun printReceipt() {
        setBusy(true)
        val emailReceiptRequest = EmailReceiptRequest(orderId.get(), email.get())

        val loginDisposable =
            receiptRepository.emailReceipt(emailReceiptRequest)
                .subscribeWith(object : DisposableSingleObserver<GenericResponse>() {
                    override fun onSuccess(t: GenericResponse) {
                        val feedbackMessage: FeedbackMessage = if (t.status) {
                            FeedbackMessage(
                                color = receiptRepository.getColor(R.color.greenUi),
                                message = t.message
                            )
                        } else {
                            FeedbackMessage(
                                color = receiptRepository.getColor(R.color.redUi),
                                message = t.message
                            )
                        }
                        _snackbarMessage.value = Event(feedbackMessage)
                        setBusy(false)
                    }

                    override fun onError(e: Throwable) {
                        setBusy(false)
                        val feedbackMessage = FeedbackMessage(
                            color = receiptRepository.getColor(R.color.redUi),
                            message = receiptRepository.getResources().getString(R.string.something_went_wrong)
                        )
                        _snackbarMessage.value = Event(feedbackMessage)
                    }
                })

        compositeDisposable.add(loginDisposable)
    }

    private fun setBusy(isBusy: Boolean) {
        busy.set(isBusy)
    }
}
