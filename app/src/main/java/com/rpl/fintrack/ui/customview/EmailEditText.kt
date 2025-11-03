package com.rpl.fintrack.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.rpl.fintrack.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val emailRegex = "[a-zA-Z0-9._%+-]+@[a-z]+\\.+[a-z]+"
    private val errorMessage = context.getString(R.string.email_error)

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                error = if (!s.toString().matches(emailRegex.toRegex())) {
                    errorMessage
                } else {
                    null
                }
            }
        })
    }

}