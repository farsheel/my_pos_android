package com.farsheel.mypos.view.category.view

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.FeedbackMessage
import com.farsheel.mypos.data.remote.response.CategoryCreateResponse
import com.farsheel.mypos.data.repository.CategoryRepository
import com.farsheel.mypos.util.Event
import com.farsheel.mypos.view.product.view.AddEditProductViewModel
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver


class AddEditCategoryViewModel(val categoryRepository: CategoryRepository) : BaseViewModel() {

    companion object {
        private val TAG = AddEditProductViewModel::class.java.simpleName
    }

    private val compositeDisposable = CompositeDisposable()

    private val _snackbarMessage = MutableLiveData<Event<FeedbackMessage>>()
    val snackbarMessage: LiveData<Event<FeedbackMessage>> get() = _snackbarMessage


    val catName = MutableLiveData<String>()
    val catDescription = MutableLiveData<String>()
    val catId = MutableLiveData<Long>()


    val nameError: ObservableField<String> = ObservableField()

    val descriptionError: ObservableField<String> = ObservableField()

    private val _saveError = MutableLiveData<Event<String>>()
    val saveError: LiveData<Event<String>> = _saveError

    val busy: ObservableField<Boolean> = ObservableField()

    fun addEditProduct() {
        var valid = true
        if (catName.value.isNullOrEmpty()) {
            nameError.set(categoryRepository.getResources().getString(R.string.name_can_not_be_empty))
            valid = false
        } else {
            nameError.set(null)
        }

        if (catDescription.value.isNullOrEmpty()) {
            descriptionError.set(categoryRepository.getResources().getString(R.string.description_can_not_be_empty))
            valid = false
        } else {
            descriptionError.set(null)
        }

        if (valid) {
            val categoryEntity = CategoryEntity(
                catId = catId.value,
                description = catDescription.value.orEmpty(),
                name = catName.value.orEmpty()
            )
            setBusy(true)
            val createDisposable =
                categoryRepository.addEditCategoryRemote(categoryEntity)
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
        busy.set(isBusy)
    }

    private fun saveCategory(categoryEntity: CategoryEntity) {
        categoryRepository.saveCategory(categoryEntity)
            .subscribe(object : SingleObserver<Long> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {

                    val feedbackMessage = FeedbackMessage(
                        color = categoryRepository.getColor(R.color.redUi),
                        message = categoryRepository.getResources().getString(R.string.something_went_wrong)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }

                override fun onSuccess(t: Long) {
                    catId.postValue(t)
                    val feedbackMessage = FeedbackMessage(
                        color = categoryRepository.getColor(R.color.greenUi),
                        message = categoryRepository.getResources().getString(R.string.category_saved)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }
            })

    }
}
