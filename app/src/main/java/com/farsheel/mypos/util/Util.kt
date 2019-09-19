package com.farsheel.mypos.util

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

//java.text.NumberFormat getPercentInstance(
class Util {
    companion object {
        @JvmStatic
        fun currencyLocale(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "GH"))
            return formatter.format(value)
        }

        fun PercentLocale(value: Double): String {
            val formatter = NumberFormat.getPercentInstance(Locale("en", "GH"))
            return formatter.format(value)
        }

        fun hideKeyboard(activity: Activity) {
            val imm =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            }
        }

        fun getDateTimeFromEpochLongOfSeconds(epoch: Long): String? {
            val formatter = SimpleDateFormat.getDateTimeInstance()
            // Create a calendar object that will convert the date and time value
            // in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = epoch
            return formatter.format(calendar.time)
        }
    }
}