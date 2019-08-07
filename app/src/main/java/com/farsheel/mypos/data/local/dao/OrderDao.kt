package com.farsheel.mypos.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farsheel.mypos.data.model.OrderDetailEntity
import com.farsheel.mypos.data.model.OrderItemEntity
import io.reactivex.Single

@Dao
interface OrderDao {

    @Query("SELECT * FROM OrderDetail WHERE OrderId = :id")
    fun findById(id: Long): OrderDetailEntity

    @Query("SELECT * FROM OrderDetail ORDER BY OrderId")
    fun getAll(): LiveData<List<OrderDetailEntity>>

    @Query("SELECT * FROM OrderDetail ORDER BY OrderId")
    fun getAllPaged(): DataSource.Factory<Int, OrderDetailEntity>

    @Query("SELECT * FROM OrderDetail WHERE OrderId LIKE :search ORDER BY OrderId")
    fun getAllPaged(search: String): DataSource.Factory<Int, OrderDetailEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(orderDetailEntity: OrderDetailEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(orderDetailEntities: List<OrderDetailEntity>)

    @Query("DELETE FROM OrderDetail WHERE OrderId = :id")
    fun deleteById(id: Long): Single<Int>

    // OrderItems

    @Query("SELECT * FROM OrderItem WHERE OrderId = :id")
    fun findOrderItemsByOrderId(id: Long): LiveData<List<OrderItemEntity>>

    @Query("SELECT * FROM OrderItem WHERE OrderId = :orderId ORDER BY OrderId")
    fun getAllOrderItemsPaged(orderId: Long): DataSource.Factory<Int, OrderItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderItem(orderItemEntity: OrderItemEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderItemList(orderItems: List<OrderItemEntity>): Single<List<Long>>
}