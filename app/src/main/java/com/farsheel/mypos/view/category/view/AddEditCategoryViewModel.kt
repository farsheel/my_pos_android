package com.farsheel.mypos.view.category.view

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.FeedbackMessage
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.response.CategoryCreateResponse
import com.farsheel.mypos.util.Event
import com.farsheel.mypos.view.product.view.AddEditProductViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class AddEditCategoryViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private val TAG = AddEditProductViewModel::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    private val _snackbarMessage = MutableLiveData<Event<FeedbackMessage>>()
    val snackbarMessage: LiveData<Event<FeedbackMessage>> get() = _snackbarMessage


    val catName = MutableLiveData<String>()
    val catDescription = MutableLiveData<String>()
    val catId = MutableLiveData<Long>()


    private val _nameError = MutableLiveData<String>()
    val nameError: LiveData<String> = _nameError

    private val _descriptionError = MutableLiveData<String>()
    val descriptionError: LiveData<String> = _descriptionError


    private val _saveError = MutableLiveData<Event<String>>()
    val saveError: LiveData<Event<String>> = _saveError

    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> get() = _busy

    fun addEditProduct() {

        var valid = true

        if (catName.value.isNullOrEmpty()) {
            _nameError.postValue(getApplication<Application>().getString(R.string.name_can_not_be_empty))
            notifyPropertyChanged(BR._all)
            valid = false
        } else {
            _nameError.postValue(null)
        }

        if (catDescription.value.isNullOrEmpty()) {
            _descriptionError.postValue(getApplication<Application>().getString(R.string.description_can_not_be_empty))
            notifyPropertyChanged(BR._all)
            valid = false
        } else {
            _descriptionError.postValue(null)
        }

        if (valid) {

            val categoryEntity = CategoryEntity(
                catId = catId.value,
                description = catDescription.value.orEmpty(),
                name = catName.value.orEmpty()
            )
            setBusy(true)
            val createDisposable =
                ApiClient.getApiService(getApplication()).createOrUpdateCategory(categoryEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<CategoryCreateResponse>() {
                        override fun onSuccess(t: CategoryCreateResponse) {
                            setBusy(false)
                            if (t.status) {
                                saveCategory(t.data)
                            } else {
                                _saveError.value = Event(t.message)
                            }
                        }

                        override fun onError(e: Throwable) {
                            setBusy(false)
                            _saveError.value = Event(e.localizedMessage)
                        }
                    })
            compositeDisposable.add(createDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun setBusy(isBusy: Boolean) {
        _busy.value = isBusy
        notifyChange()
    }

    private fun saveCategory(categoryEntity: CategoryEntity) {
        AppDatabase.invoke(getApplication()).categoryDao().insert(categoryEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Long> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {

                    val feedbackMessage = FeedbackMessage(
                        color = ContextCompat.getColor(getApplication(), R.color.redUi),
                        message = getApplication<Application>().getString(R.string.something_went_wrong)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }

                override fun onSuccess(t: Long) {
                    catId.postValue(t)
                    val feedbackMessage = FeedbackMessage(
                        color = ContextCompat.getColor(getApplication(), R.color.greenUi),
                        message = getApplication<Application>().getString(R.string.category_saved)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }
            })

    }
}
