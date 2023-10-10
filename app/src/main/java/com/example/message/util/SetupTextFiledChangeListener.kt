package com.example.message.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

fun addTextChangeListeners(fields: List<TextInputLayout>) {
    for (layout in fields) {
        val editText = layout.editText

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text changes.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text changes.
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text changes.

                // Check if the text is not empty
                val isNotEmpty = s?.isNotEmpty() == true

                // If the text is not empty, clear the error
                if (isNotEmpty) {
                    layout.error = null
                }
            }
        }

        editText?.addTextChangedListener(textWatcher)
    }
}
