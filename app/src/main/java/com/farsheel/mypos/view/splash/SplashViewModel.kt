package com.farsheel.mypos.view.splash

import android.app.Application
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.PreferenceManager
import com.farsheel.mypos.util.Event

class SplashViewModel(application: Application) : BaseViewModel(application) {

    private val _openNext = MutableLiveData<Event<Int>>()
    val openNext: LiveData<Event<Int>> get() = _openNext

    init {
        Handler().postDelayed({
            if (PreferenceManager.isUserSessionAvailable(application)) {
                _openNext.postValue(Event(R.id.action_splashFragment_to_homeFragment))
            } else {
                _openNext.postValue(Event(R.id.action_splashFragment_to_loginFragment))
            }

        }, 500)

    }
}
