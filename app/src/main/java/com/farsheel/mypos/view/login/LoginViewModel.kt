package com.farsheel.mypos.view.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.PreferenceManager
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.request.LoginRequest
import com.farsheel.mypos.data.remote.response.LoginResponse
import com.farsheel.mypos.data.work.SyncWorkManager
import com.farsheel.mypos.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class LoginViewModel(application: Application) : BaseViewModel(application) {

    private var disposable: CompositeDisposable = CompositeDisposable()
    val email: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()


    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> get() = _busy

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage


    private val _loginSuccess = MutableLiveData<Event<Boolean>>()
    val loginSuccess: LiveData<Event<Boolean>> get() = _loginSuccess

    init {
        setBusy(false)
    }

    fun onClickLogin() {
        setBusy(true)

        val loginRequest = LoginRequest(email.value.toString(), password.value.toString())

        val loginDisposable = ApiClient.getApiService(getApplication()).login(loginRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<LoginResponse>() {
                override fun onSuccess(t: LoginResponse) {
                    if (t.status) {
                        PreferenceManager.setUserSession(getApplication(), t)
                        scheduleWork()
                        _loginSuccess.value = Event(true)
                    } else {
                        _errorMessage.postValue(Event(t.message))
                    }
                    setBusy(false)
                }

                override fun onError(e: Throwable) {
                    setBusy(false)
                    _errorMessage.postValue(Event(e.localizedMessage))
                }
            })

        disposable.add(loginDisposable)
    }

    private fun setBusy(isBusy: Boolean) {
        _busy.value = isBusy
        notifyChange()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
