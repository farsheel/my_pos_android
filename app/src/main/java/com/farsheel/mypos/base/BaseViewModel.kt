package com.farsheel.mypos.base

import android.app.Application
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.farsheel.mypos.data.work.SyncWorkManager
import java.util.concurrent.TimeUnit


open class BaseViewModel(application: Application) : AndroidViewModel(application), Observable {


    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()


    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }


    protected fun scheduleWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = PeriodicWorkRequestBuilder<SyncWorkManager>(15, TimeUnit.MINUTES)
        work.addTag(SyncWorkManager::class.java.simpleName)
        work.setConstraints(constraints)

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            SyncWorkManager::class.java.simpleName,
            ExistingPeriodicWorkPolicy.REPLACE,
            work.build()
        )
    }

    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.remove(callback)
    }
}