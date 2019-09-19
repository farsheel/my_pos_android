package com.farsheel.mypos.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.farsheel.mypos.base.BaseViewModel
import com.farsheel.mypos.data.model.CartEntity
import com.farsheel.mypos.data.model.CategoryEntity
import com.farsheel.mypos.data.model.ProductEntity
import com.farsheel.mypos.data.repository.CartRepository
import com.farsheel.mypos.data.repository.CategoryRepository
import com.farsheel.mypos.data.repository.ProductRepository
import com.farsheel.mypos.util.Event

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val categoryRepository: CategoryRepository

) : BaseViewModel() {


    fun getCategoryList(): LiveData<List<CategoryEntity>> {
        return categoryRepository.getCategories()
    }

    fun selectCategory(catId: Long?) {
        productRepository.selectedCat.postValue(catId)
    }

    fun getProductList(): LiveData<PagedList<ProductEntity>> {
        return productRepository.productList
    }

    fun getCartList(): LiveData<List<CartEntity>> {
        return cartRepository.cartListLive
    }

    fun getCartTotal(): LiveData<Double> {
        return cartRepository.cartSubTotal
    }


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
        cartRepository.addToCart(cartEntity)
    }

    fun onClickCart() {
        _navigateToCart.postValue(Event(true))

    }

    fun clearCart() {
        cartRepository.clearCart()
    }
}
