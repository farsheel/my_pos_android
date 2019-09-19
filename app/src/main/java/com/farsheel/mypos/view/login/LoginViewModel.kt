package com.farsheel.mypos.view.login

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.remote.request.LoginRequest
import com.farsheel.mypos.data.remote.response.LoginResponse
import com.farsheel.mypos.data.repository.AuthRepository
import com.farsheel.mypos.data.repository.WorkRepository
import com.farsheel.mypos.util.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver


class LoginViewModel(val authRepository: AuthRepository,val workRepository: WorkRepository) : BaseViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    val email: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()

    val busy: ObservableField<Boolean> = ObservableField()

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

        val loginDisposable = authRepository.login(loginRequest)
            .subscribeWith(object : DisposableSingleObserver<LoginResponse>() {
                override fun onSuccess(t: LoginResponse) {
                    if (t.status) {
                        authRepository.setUserSession(t)
                        workRepository.scheduleSyncWork()
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
        busy.set(isBusy)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
