package com.example.message.util

import com.google.android.material.textfield.TextInputLayout

fun validateTextInputLayouts(fields: List<Pair<TextInputLayout, String>>): Boolean {
    for (fieldPair in fields) {
        val field = fieldPair.first
        val errorMessage = fieldPair.second

        val text = field.editText?.text.toString().trim()

        if (text.isEmpty()) {
            showErrorAndFocus(field, errorMessage)
            return false
        }
    }
    return true
}

private fun showErrorAndFocus(layout: TextInputLayout, errorMessage: String) {
    layout.error = errorMessage
    layout.editText?.requestFocus()
}

