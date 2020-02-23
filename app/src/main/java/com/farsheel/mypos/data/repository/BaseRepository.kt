package com.farsheel.mypos.data.repository

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

open class BaseRepository(val context: Context) {

    fun getResources(): Resources {
        return context.resources
    }

    fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }
}