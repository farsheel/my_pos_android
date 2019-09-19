package com.farsheel.mypos.view.splash

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.repository.AuthRepository
import com.farsheel.mypos.util.Event

class SplashViewModel(authRepository: AuthRepository) : BaseViewModel() {

    private val _openNext = MutableLiveData<Event<Int>>()
    val openNext: LiveData<Event<Int>> get() = _openNext

    init {
        Handler().postDelayed({
            if (authRepository.isUserSessionAvailable()) {
                _openNext.postValue(Event(R.id.action_splashFragment_to_homeFragment))
            } else {
                _openNext.postValue(Event(R.id.action_splashFragment_to_loginFragment))
            }

        }, 500)

    }
}
