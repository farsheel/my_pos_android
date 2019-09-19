package com.farsheel.mypos.data.repository

import android.content.Context
import androidx.paging.LivePagedListBuilder
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.model.CartEntity
import io.reactivex.schedulers.Schedulers

class CartRepository(context: Context, private val appDatabase: AppDatabase) :
    BaseRepository(context) {

    val cartList = LivePagedListBuilder(
        appDatabase.cartDao().getAllPaged(),
        20
    ).build()

    val cartListLive = appDatabase.cartDao().getAll()

    val cartSubTotal = appDatabase.cartDao().getCartTotal()

    val cartSingleAll = appDatabase.cartDao().getSingleAll()

    val  cartVatTotalPay = appDatabase.cartDao().getCartVatTotalPay()

    val  cartVatTotal = appDatabase.cartDao().getCartVatTotal()

    fun addToCart(cartEntity: CartEntity) {
        appDatabase.cartDao().insert(cartEntity)
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun removeCart(cartEntity: CartEntity) {
        appDatabase.cartDao().deleteById(cartEntity.id)
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun clearCart() {
        appDatabase.cartDao().deleteAll().subscribeOn(Schedulers.io())
            .subscribe()
    }
}