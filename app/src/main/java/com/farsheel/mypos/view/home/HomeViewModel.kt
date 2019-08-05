package com.farsheel.mypos.view.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CartEntity
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.util.Event
import io.reactivex.schedulers.Schedulers

class HomeViewModel(application: Application) : BaseViewModel(application) {

    val selectedCat: MutableLiveData<Long> = MutableLiveData()
    val categoryList: LiveData<List<CategoryEntity>> =
        AppDatabase.invoke(getApplication()).categoryDao().getAllList()

    val productList: LiveData<PagedList<ProductEntity>> = Transformations.switchMap(selectedCat) {
        if (it == null) {
            return@switchMap LivePagedListBuilder(
                AppDatabase.invoke(getApplication()).productDao().getAll(),
                20
            ).build()

        } else {
            return@switchMap LivePagedListBuilder(
                AppDatabase.invoke(getApplication()).productDao().getAllByCatId(selectedCat.value),
                20
            ).build()
        }
    }

    val cartList = AppDatabase.invoke(application).cartDao().getAll()

    val cartTotal = AppDatabase.invoke(application).cartDao().getCartTotal()


    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>> get() = _navigateToCart

    fun addToCart(productEntity: ProductEntity) {
        val cartEntity = CartEntity(
            id = 0,
            name = productEntity.name,
            quantity = 1.0,
            productId = productEntity.itemId!!,
            productPrice = productEntity.price
        )
        AppDatabase.invoke(getApplication()).cartDao().insert(cartEntity)
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun onClickCart() {
        _navigateToCart.postValue(Event(true))

    }

    fun clearCart() {
        AppDatabase.invoke(getApplication()).cartDao().deleteAll().subscribeOn(Schedulers.io())
            .subscribe()
    }
}
