package com.jordansilva.imageloader.util.extension

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.jordansilva.imageloader.R

fun EditText.clearButtonWithAction(block: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val clearIcon = if (editable?.isNotEmpty() == true) ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24) else null
            setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], clearIcon, compoundDrawables[3])
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                setText("")
                block()
                requestFocus()
                true
            }
        }
        false
    }
}

fun EditText.onEditorAction(action: Int, block: () -> Unit) {
    setOnEditorActionListener { _, actionId, keyEvent ->
        dismissKeyboard()
        if (actionId == action || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
            block()
            clearFocus()
            true
        } else {
            false
        }
    }
}

fun EditText.dismissKeyboard() {
    val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}