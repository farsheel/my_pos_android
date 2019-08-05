package com.farsheel.mypos.data.local.dao

import androidx.paging.DataSource
import androidx.room.*
import com.farsheel.mypos.data.model.ProductEntity
import io.reactivex.Single

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product WHERE itemId = :itemId")
    fun findById(itemId: Int): ProductEntity

    @Query("SELECT * FROM Product ORDER BY ItemName")
    fun getAll(): DataSource.Factory<Int, ProductEntity>

    @Query("SELECT * FROM Product WHERE ItemName LIKE :search ORDER BY ItemName")
    fun getAllByQuery(vararg search: String): DataSource.Factory<Int, ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productEntity: ProductEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(todo: List<ProductEntity>)

    @Delete
    fun delete(productEntity: ProductEntity)

    @Query("SELECT * FROM Product WHERE ItemCategory = :catId ORDER BY ItemName")
    fun getAllByCatId(catId: Long?): DataSource.Factory<Int, ProductEntity>
}