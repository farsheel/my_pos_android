package com.farsheel.mypos.util

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
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

        @JvmStatic
        fun currencyLocale3Fraction(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "GH"))
            formatter.maximumFractionDigits = 3
            formatter.minimumFractionDigits = 3
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


         fun generateQR(Value: String): Bitmap? {
            var bitMatrix: BitMatrix? = null
            try {
                val multiFormatWriter = MultiFormatWriter()
                //      val barcodeFormat = BarcodeFormat.DATA_MATRIX
                bitMatrix = multiFormatWriter.encode(Value, BarcodeFormat.QR_CODE, 1000, 1000, null)
            } catch (e: WriterException) {
                try {
                    e.printStackTrace()
                } catch (e2: IllegalArgumentException) {
                    return null
                }

            }

            val bitMatrixWidth = bitMatrix!!.width
            val bitMatrixHeight = bitMatrix.height
            val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
            for (y in 0 until bitMatrixHeight) {
                val offset = y * bitMatrixWidth
                for (x in 0 until bitMatrixWidth) {
                    val color: Int = if (bitMatrix.get(x, y)) {
                        Color.BLACK
                    } else {

                        Color.WHITE
                    }
                    val i = offset + x
                    pixels[i] = color
                }
            }
            val bitmap =
                Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, 1000, 0, 0, bitMatrixWidth, bitMatrixHeight)
            return bitmap
        }
    }
}