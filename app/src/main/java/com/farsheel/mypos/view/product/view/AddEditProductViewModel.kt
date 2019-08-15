package com.farsheel.mypos.view.product.view

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.work.*
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.FeedbackMessage
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.response.ProductCreateResponse
import com.farsheel.mypos.data.work.ProductImageUploadWork
import com.farsheel.mypos.data.work.SyncWorkManager
import com.farsheel.mypos.util.Event
import com.farsheel.mypos.view.category.view.AddEditCategoryViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.File

class AddEditProductViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private val TAG = AddEditCategoryViewModel::class.java.simpleName
    }


    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var categoryFilter = MutableLiveData<String>()

    val categoryList: LiveData<PagedList<CategoryEntity>> =
        Transformations.switchMap(categoryFilter) {
            if (it.isNullOrEmpty()) {
                return@switchMap LivePagedListBuilder(
                    AppDatabase.invoke(getApplication()).categoryDao().getAll(), 20
                ).build()
            } else {
                return@switchMap LivePagedListBuilder(
                    AppDatabase.invoke(getApplication()).categoryDao().getAllByQuery("%$it%"), 20
                ).build()
            }
        }

    private val _showCatSpinner = MutableLiveData<Event<Boolean>>()
    val showCatSpinner: LiveData<Event<Boolean>> get() = _showCatSpinner

    private val _selectImage = MutableLiveData<Event<Boolean>>()
    val selectImage: LiveData<Event<Boolean>> get() = _selectImage

    val itemUpc = MutableLiveData<String>()
    val itemName = MutableLiveData<String>()
    val itemId = MutableLiveData<Long>()
    val price = MutableLiveData<String>()
    val category = MutableLiveData<String>()
    val categoryString = MutableLiveData<String>()
    val image  = MutableLiveData<File?>()

    val description = MutableLiveData<String>()


    val mUpcError = MutableLiveData<String>()
    val upcError: LiveData<String> = mUpcError

    val mNameError = MutableLiveData<String>()
    val nameError: LiveData<String> = mNameError

    val mPriceError = MutableLiveData<String>()
    val priceError: LiveData<String> = mPriceError

    val mCategoryError = MutableLiveData<String>()
    val categoryError: LiveData<String> = mCategoryError

    private val _snackbarMessage = MutableLiveData<Event<FeedbackMessage>>()
    val snackbarMessage: LiveData<Event<FeedbackMessage>> get() = _snackbarMessage

    private val _saveError = MutableLiveData<Event<String>>()
    val saveError: LiveData<Event<String>> = _saveError

    private val _busy = MutableLiveData<Boolean>()
    val busy: LiveData<Boolean> get() = _busy


    fun addEditProduct() {

        if (validate(true)) {
            val productEntity = ProductEntity(
                itemId = itemId.value,
                upc = itemUpc.value.orEmpty(),
                category = category.value!!,
                description = description.value.orEmpty(),
                name = itemName.value.orEmpty(),
                price = if (price.value.isNullOrEmpty()) 0.0 else price.value!!.toDouble(),
                image = ""
            )

            setBusy(true)
            val createDisposable =
                ApiClient.getApiService(getApplication()).createOrUpdateProduct(productEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<ProductCreateResponse>() {
                        override fun onSuccess(t: ProductCreateResponse) {
                            if (t.status) {
                                saveProduct(t.data)
                            } else {
                                setBusy(false)
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

    private fun uploadImage(itemId: Long?) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequestBuilder<ProductImageUploadWork>()
        work.addTag(SyncWorkManager::class.java.simpleName)
        work.setConstraints(constraints)
        val data = Data.Builder()
//Add parameter in Data class. just like bundle. You can also add Boolean and Number in parameter.
        data.putString("file_path", image?.value.toString())
        data.putString("product_id", itemId.toString())
//Set Input Data
        work.setInputData(data.build())
        WorkManager.getInstance(getApplication()).enqueue(work.build())
    }

    fun validate(notify: Boolean): Boolean {
        var valid = true

        if (itemUpc.value.isNullOrEmpty()) {
            mUpcError.postValue(getApplication<Application>().getString(R.string.upc_can_not_empty))
            valid = false

        } else {
            mUpcError.postValue(null)
        }
        if (itemName.value.isNullOrEmpty()) {
            mNameError.postValue(getApplication<Application>().getString(R.string.name_can_not_be_empty))
            valid = false
        } else {
            mNameError.postValue(null)
        }
        if (price.value.isNullOrEmpty()) {
            mPriceError.postValue(getApplication<Application>().getString(R.string.price_can_not_be_empty))
            valid = false
        } else {
            mPriceError.postValue(null)
        }
        if (category.value.isNullOrEmpty()) {
            mCategoryError.postValue(getApplication<Application>().getString(R.string.please_select_category))
            valid = false
        } else {
            mCategoryError.postValue(null)
        }
        if (notify) {
            notifyChange()
        }

        return valid
    }

    private fun saveProduct(productEntity: ProductEntity) {
        if (image.value != null) {
            uploadImage(productEntity.itemId)
        }

        AppDatabase.invoke(getApplication()).productDao().insert(productEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Long> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    setBusy(false)

                    val feedbackMessage = FeedbackMessage(
                        color = ContextCompat.getColor(getApplication(), R.color.redUi),
                        message = getApplication<Application>().getString(R.string.something_went_wrong)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }

                override fun onSuccess(t: Long) {
                    setBusy(false)
                    itemId.postValue(t)
                    val feedbackMessage = FeedbackMessage(
                        color = ContextCompat.getColor(getApplication(), R.color.greenUi),
                        message = getApplication<Application>().getString(R.string.product_saved)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }
            })
    }

    fun showCategories() {
        _showCatSpinner.postValue(Event(true))
    }

    fun selectImage() {
        _selectImage.postValue(Event(true))
    }

    private fun setBusy(isBusy: Boolean) {
        _busy.value = isBusy
        notifyChange()
    }

}
