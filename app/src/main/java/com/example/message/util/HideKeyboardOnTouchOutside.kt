package com.example.message.util

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout

fun setupHideKeyboardOnTouchOutside(editTextList: List<TextInputLayout>, layout: View) {
    layout.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            for (editTextLayout in editTextList) {
                val editText = editTextLayout.editText
                if (editText!!.isFocused) {
                    val outRect = Rect()
                    editText.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        editText.clearFocus()
                        val imm =
                            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    }
                }
            }
        }
        layout.performClick()
        false
    }
}
