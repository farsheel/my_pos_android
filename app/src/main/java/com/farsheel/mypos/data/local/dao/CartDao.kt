package com.farsheel.mypos.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farsheel.mypos.data.model.CartEntity
import io.reactivex.Single


@Dao
interface CartDao {
    @Query("SELECT * FROM Cart WHERE id = :id")
    fun findById(id: Long): CartEntity

    @Query("SELECT * FROM Cart ORDER BY id")
    fun getAll(): LiveData<List<CartEntity>>

    @Query("SELECT * FROM Cart")
    fun getSingleAll(): Single<List<CartEntity>>


    @Query("SELECT * FROM Cart ORDER BY id")
    fun getAllPaged(): DataSource.Factory<Int, CartEntity>

    @Query("SELECT SUM(Quantity*ProductPrice) FROM Cart")
    fun getCartTotal(): LiveData<Double>


    @Query("SELECT SUM(0.029*Quantity*ProductPrice) FROM Cart")
    fun getCartVatTotal(): LiveData<Double>


    @Query("SELECT SUM((0.029*Quantity*ProductPrice)+(Quantity*ProductPrice)) FROM Cart")
    fun getCartVatTotalPay(): LiveData<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cartEntity: CartEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(carts: List<CartEntity>)

    @Query("DELETE FROM CART WHERE id = :id")
    fun deleteById(id: Long): Single<Int>

    @Query("DELETE FROM CART")
    fun deleteAll(): Single<Int>
}