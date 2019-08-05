package com.farsheel.mypos.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.farsheel.mypos.data.model.CategoryEntity
import io.reactivex.Single


@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category WHERE CategoryId = :catId")
    fun findById(catId: Long): CategoryEntity

    @Query("SELECT * FROM Category ORDER BY CategoryName")
    fun getAll(): DataSource.Factory<Int, CategoryEntity>

    @Query("SELECT * FROM Category WHERE CategoryName LIKE :search ORDER BY CategoryName")
    fun getAllByQuery(vararg search: String): DataSource.Factory<Int, CategoryEntity>

    @Query("SELECT * FROM Category ORDER BY CategoryName")
    fun getAllList(): LiveData<List<CategoryEntity>>

    @Query("SELECT CategoryName FROM Category ORDER BY CategoryName")
    fun getAllStringList(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productEntity: CategoryEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(todo: List<CategoryEntity>)

    @Delete
    fun delete(productEntity: CategoryEntity)

}