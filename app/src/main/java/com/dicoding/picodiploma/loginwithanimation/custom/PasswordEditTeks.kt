package com.dicoding.picodiploma.loginwithanimation.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.material.textfield.TextInputLayout

class PasswordEditTeks : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p: CharSequence, start: Int, before: Int, count: Int) {
                val password = p.toString()

                if (password.length < 8) {
                    (parent.parent as TextInputLayout).error =
                        context.getString(R.string.lessfrom8)
                } else {
                    (parent.parent as TextInputLayout).error = null
                }
            }

            override fun afterTextChanged(p: Editable?) {

            }

        })
    }
}