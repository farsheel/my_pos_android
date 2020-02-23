package com.farsheel.mypos.data.repository

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.farsheel.mypos.data.work.SyncWorkManager

class WorkRepository(context: Application) :
    BaseRepository(context) {

    fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequestBuilder<SyncWorkManager>()
        work.addTag(SyncWorkManager::class.java.simpleName)
        work.setConstraints(constraints)
        WorkManager.getInstance(context).enqueue(work.build())
    }
}