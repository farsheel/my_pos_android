package com.farsheel.mypos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import com.farsheel.mypos.data.local.dao.CartDao
import com.farsheel.mypos.data.local.dao.CategoryDao
import com.farsheel.mypos.data.local.dao.OrderDao
import com.farsheel.mypos.data.local.dao.ProductDao
import com.farsheel.mypos.data.model.*

@Database(
    entities = [ProductEntity::class, CategoryEntity::class, CartEntity::class, OrderDetailEntity::class, OrderItemEntity::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "my-pos.db"
        ).fallbackToDestructiveMigration().build()
    }

    fun clearAndResetAllTables() {

        // reset all auto-incrementalValues
        val query = SimpleSQLiteQuery("DELETE FROM sqlite_sequence")

        instance?.runInTransaction {
            instance?.clearAllTables()
            instance?.query(query)

        }

    }
}