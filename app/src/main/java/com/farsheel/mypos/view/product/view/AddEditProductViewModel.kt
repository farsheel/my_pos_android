package com.farsheel.mypos.view.product.view

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.farsheel.mypos.R
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.FeedbackMessage
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.remote.response.ProductCreateResponse
import com.farsheel.mypos.data.repository.CategoryRepository
import com.farsheel.mypos.data.repository.ProductRepository
import com.farsheel.mypos.util.Event
import com.farsheel.mypos.view.category.view.AddEditCategoryViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import java.io.File

class AddEditProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : BaseViewModel() {

    companion object {
        private val TAG = AddEditCategoryViewModel::class.java.simpleName
    }


    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    var categoryFilter = MutableLiveData<String>()


    private val _showCatSpinner = MutableLiveData<Event<Boolean>>()
    val showCatSpinner: LiveData<Event<Boolean>> get() = _showCatSpinner

    private val _selectImage = MutableLiveData<Event<Boolean>>()
    val selectImage: LiveData<Event<Boolean>> get() = _selectImage

    val itemUpc = MutableLiveData<String>()
    val itemName = MutableLiveData<String>()
    val itemId = MutableLiveData<Long>()
    val price = MutableLiveData<String>()
    val category = ObservableField<String>()
    val categoryString = ObservableField<String>()
    val image = MutableLiveData<File?>()

    val description = MutableLiveData<String>()


    val upcError: ObservableField<String> = ObservableField()
    val nameError: ObservableField<String> = ObservableField()
    val priceError: ObservableField<String> = ObservableField()
    val categoryError: ObservableField<String> = ObservableField()

    private val _snackbarMessage = MutableLiveData<Event<FeedbackMessage>>()
    val snackbarMessage: LiveData<Event<FeedbackMessage>> get() = _snackbarMessage

    private val _saveError = MutableLiveData<Event<String>>()
    val saveError: LiveData<Event<String>> = _saveError

    val busy = ObservableField<Boolean>()


    fun categoryList(): LiveData<PagedList<CategoryEntity>> {
        return categoryRepository.getPagedCategories()
    }

    fun searchCategory(search: String?) {
        categoryRepository.filterTextAll.postValue(search)
    }

    fun addEditProduct() {
        if (validate()) {
            val productEntity = ProductEntity(
                itemId = itemId.value,
                upc = itemUpc.value.orEmpty(),
                category = category.get()!!,
                description = description.value.orEmpty(),
                name = itemName.value.orEmpty(),
                price = if (price.value.isNullOrEmpty()) 0.0 else price.value!!.toDouble(),
                image = ""
            )
            setBusy(true)
            val createDisposable =
                productRepository.addEditProductRemote(productEntity)
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


    fun validate(): Boolean {
        var valid = true

        if (itemUpc.value.isNullOrEmpty()) {
            upcError.set(productRepository.getResources().getString(R.string.upc_can_not_empty))
            valid = false

        } else {
            upcError.set(null)
        }
        if (itemName.value.isNullOrEmpty()) {
            nameError.set(productRepository.getResources().getString(R.string.name_can_not_be_empty))
            valid = false
        } else {
            nameError.set(null)
        }
        if (price.value.isNullOrEmpty()) {
            priceError.set(productRepository.getResources().getString(R.string.price_can_not_be_empty))
            valid = false
        } else {
            priceError.set(null)
        }
        if (category.get().isNullOrEmpty()) {
            categoryError.set(productRepository.getResources().getString(R.string.please_select_category))
            valid = false
        } else {
            categoryError.set(null)
        }

        return valid
    }

    private fun saveProduct(productEntity: ProductEntity) {
        if (image.value != null) {
            productRepository.uploadProductImage(productEntity.itemId, image.value.toString())
        }

        productRepository.saveProductLocal(productEntity)
            .subscribe(object : SingleObserver<Long> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    setBusy(false)

                    val feedbackMessage = FeedbackMessage(
                        color = productRepository.getColor(R.color.redUi),
                        message = productRepository.getResources().getString(R.string.something_went_wrong)
                    )
                    _snackbarMessage.postValue(Event(feedbackMessage))
                }

                override fun onSuccess(t: Long) {
                    setBusy(false)
                    itemId.postValue(t)
                    val feedbackMessage = FeedbackMessage(
                        color = productRepository.getColor(R.color.greenUi),
                        message = productRepository.getResources().getString(R.string.product_saved)
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
        busy.set(isBusy)
    }
}
