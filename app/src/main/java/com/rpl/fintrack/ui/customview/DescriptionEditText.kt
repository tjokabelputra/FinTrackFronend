package com.rpl.fintrack.ui.customview

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.rpl.fintrack.R

class DescriptionEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs) {
    private val maxChars = 350
    private val errorMessage = "Name cannot be more than $maxChars characters"
    private var charCountView: TextView? = null

    init {
        filters = arrayOf(InputFilter.LengthFilter(maxChars))

        addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                val remaining = maxChars - length

                charCountView?.text = context.getString(
                    R.string.characters_left_format, remaining
                )

                if(length > maxChars){
                    error = errorMessage
                }
                else{
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    fun setCharCountView(view: TextView){
        charCountView = view
        val remaining = maxChars - (text?.length ?: 0)
        view.text = context.getString(R.string.characters_left_format, remaining)
    }
}