package com.farsheel.mypos.util

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("errorTextString")
fun TextInputLayout.setErrorText(errorMessage: String?) {
    if (errorMessage.isNullOrEmpty()) {
        this.isErrorEnabled = false
        this.error = null
    } else {
        this.isErrorEnabled = true
        this.error = errorMessage
    }

}

@BindingAdapter("attachFloatingButton")
fun bindRecyclerViewWithFB(recyclerView: RecyclerView, fb: FloatingActionButton) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && fb.isShown) {
                fb.hide()
            } else if (dy < 0 && !fb.isShown) {
                fb.show()
            }
        }
    })
}

@BindingAdapter("removeErrorOnTyping")
fun removeErrorOnTyping(text: TextInputEditText,textInputLayout: TextInputLayout){
    text.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            textInputLayout.setErrorText(null)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })

}


