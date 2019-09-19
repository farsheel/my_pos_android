package com.farsheel.mypos.data.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.farsheel.mypos.R
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.remote.ApiClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ProductImageUploadWork(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val notificationId: Int = System.currentTimeMillis().toInt()
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder

    override fun doWork(): Result {

        val filePath = inputData.getString("file_path")
        val file = File(filePath)

        val progressRequestBody = ProgressRequestBody(file, object : UploadCallbacks {
            override fun onProgressUpdate(percentage: Int) {
                Log.d("ProductImageUploadWork", percentage.toString())
                mBuilder.setProgress(100, percentage, false)
                mNotificationManager.notify(notificationId, mBuilder.build())

            }

            override fun onError() {
                mBuilder.setContentTitle("Image upload failed")
                val pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.productListFragment)
                    .createPendingIntent()
                mBuilder.setContentIntent(pendingIntent)
                mNotificationManager.notify(notificationId, mBuilder.build())
            }

            override fun onFinish() {
                mNotificationManager.cancelAll()
            }

            override fun uploadStart() {

            }
        })

        onEvent()

        val filePart =
            MultipartBody.Part.createFormData("product_image", file.name, progressRequestBody)

        inputData.getString("product_id")?.let {
            ApiClient.getApiService(applicationContext).uploadProductImage(
                it, filePart
            ).subscribe { product ->
                if (product.status) {
                    AppDatabase.invoke(context).productDao().insert(product.data).subscribe()
                }
            }
        }


        return Result.success()
    }


    inner class ProgressRequestBody(
        private val mFile: File,
        private val mListener: UploadCallbacks
    ) : RequestBody() {
        init {
            mListener.uploadStart()
        }

        override fun contentType(): MediaType? {
      return "*/*".toMediaTypeOrNull()
        }


        @Throws(IOException::class)
        override fun contentLength(): Long {
            return mFile.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val fileLength = mFile.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val inputStream = FileInputStream(mFile)
            var uploaded: Long = 0

            try {
                var read: Int
                val handler = Handler(Looper.getMainLooper())
                while ((inputStream.read(buffer).also { read = it }) != -1) {
                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                    handler.post(ProgressUpdater(uploaded, fileLength))
                }
            } finally {
                inputStream.close()
            }
        }

        private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
            Runnable {

            override fun run() {
                try {

                    val progress = (100 * mUploaded / mTotal).toInt()

                    if (progress == 100)
                        mListener.onFinish()
                    else
                        mListener.onProgressUpdate(progress)
                } catch (e: ArithmeticException) {
                    mListener.onError()
                    e.printStackTrace()
                }

            }
        }


    }

    fun onEvent() {

        val mContext = applicationContext
        val resultIntent = Intent()
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
        mBuilder.setSmallIcon(R.drawable.ic_history_white_24dp)
        mBuilder.color = ContextCompat.getColor(mContext, R.color.colorPrimary)
        mBuilder.setContentTitle(applicationContext.getString(R.string.uploading))
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
                NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
            notificationChannel.description = "no sound"
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mBuilder.setProgress(100, 0, false)
        mNotificationManager.notify(notificationId, mBuilder.build())
    }


    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)

        fun onError()

        fun onFinish()

        fun uploadStart()
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048

        const val NOTIFICATION_CHANNEL_ID = "my_pos_channel_1"
        const val NOTIFICATION_CHANNEL_NAME = "my_pos_channel"
    }

}