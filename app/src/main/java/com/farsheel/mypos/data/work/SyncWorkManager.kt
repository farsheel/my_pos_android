package com.farsheel.mypos.data.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.farsheel.mypos.R
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.remote.ApiClient
import com.farsheel.mypos.data.remote.response.CategoryResponse
import com.farsheel.mypos.data.remote.response.ProductResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver

class SyncWorkManager(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val disposables = CompositeDisposable()

    private val notificationId: Int = System.currentTimeMillis().toInt()
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder

    override fun doWork(): Result {
        try {
            Thread.sleep(200)
        } catch (e: Exception) {
        }
        showNotification()
        mBuilder.setContentTitle(applicationContext.getString(R.string.fetching_categories))
        mNotificationManager.notify(notificationId, mBuilder.build())
        syncCategory(1)

        mBuilder.setContentTitle(applicationContext.getString(R.string.fetching_products))
        mNotificationManager.notify(notificationId, mBuilder.build())
        syncProduct(1)
        mNotificationManager.cancelAll()

        disposables.dispose()
        disposables.clear()
        return Result.success()
    }

    private fun syncProduct(page: Int) {

        val productDispose = ApiClient.getApiService(applicationContext).product(page)
            .subscribeWith(object : DisposableSingleObserver<ProductResponse>() {
                override fun onSuccess(t: ProductResponse) {
                    t.data?.let {
                        AppDatabase.invoke(applicationContext).productDao().insertAll(it)
                    }
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                    }
                    if (t.data.isNullOrEmpty()) {
                        return
                    }
                    if (t.lastPage != page) {
                        syncProduct((page + 1))
                    }
                }

                override fun onError(e: Throwable) {
                }

            })
        disposables.add(productDispose)
    }

    private fun syncCategory(page: Int) {
        val categoryDispose = ApiClient.getApiService(applicationContext).category(page)
            .subscribeWith(object : DisposableSingleObserver<CategoryResponse>() {
                override fun onSuccess(t: CategoryResponse) {
                    t.data?.let {
                        AppDatabase.invoke(applicationContext).categoryDao().insertAll(it)
                    }
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                    }
                    if (t.data.isNullOrEmpty()) {
                        return
                    }
                    if (t.lastPage != page) {
                        syncCategory((page + 1))
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
        disposables.add(categoryDispose)
    }

    private fun showNotification() {

        val mContext = applicationContext
        val resultIntent = Intent()
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder = NotificationCompat.Builder(
            mContext,
            ProductImageUploadWork.NOTIFICATION_CHANNEL_ID
        )
        mBuilder.setSmallIcon(R.drawable.ic_history_white_24dp)
        mBuilder.color = ContextCompat.getColor(mContext, R.color.colorPrimary)
        mBuilder.setContentTitle(applicationContext.getString(R.string.syncing))
            .setOngoing(true)
            .setContentText(applicationContext.getString(R.string.in_progress))
            .setContentIntent(resultPendingIntent).priority = NotificationCompat.PRIORITY_HIGH

        mBuilder.setSound(null)
        mBuilder.setVibrate(longArrayOf(0L))
        mBuilder.build().flags = mBuilder.build().flags or Notification.FLAG_ONGOING_EVENT
        mBuilder.setWhen(System.currentTimeMillis())
        mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(
                    ProductImageUploadWork.NOTIFICATION_CHANNEL_ID,
                    ProductImageUploadWork.NOTIFICATION_CHANNEL_NAME,
                    importance
                )
            notificationChannel.description = "no sound"
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            mBuilder.setChannelId(ProductImageUploadWork.NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mBuilder.setProgress(100, 0, true)
        mNotificationManager.notify(notificationId, mBuilder.build())
    }
}