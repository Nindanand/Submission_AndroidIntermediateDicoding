package com.example.aplikasistory.view.customview

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import android.util.AttributeSet as AttributeSet1

class MyEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet1) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet1, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(onTextChanged = { s, _, _, _ ->
            if (s.toString().length < 8) {
                setError("Password must not be less than 8 characters", null)
            } else {
                error = null
            }
        })
    }
}