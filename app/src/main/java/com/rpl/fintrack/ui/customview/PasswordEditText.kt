package com.rpl.fintrack.ui.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?= null
) : AppCompatEditText(context, attrs) {

    private val errorMessage = "Password cannot be less than 8 characters"

    override fun onTextChanged(
        s: CharSequence, start: Int, before: Int, count: Int
    ) {
        if(s.toString().length < 8){
            setError(errorMessage, null)
        }
        else{
            error = null
        }
    }
}