package com.rockytauhid.storyapps.view.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.rockytauhid.storyapps.R

class PasswordEditText : TextInputEditText, View.OnFocusChangeListener {

    companion object {
        private const val MIN_LENGTH = 6
    }

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.enter_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus)
            isValid()
    }

    fun isValid(): Boolean {
        error = when {
            text.isNullOrEmpty() -> {
                resources.getString(R.string.enter_password)
            }
            (text?.length ?: 0) < MIN_LENGTH -> {
                resources.getString(R.string.invalid_password)
            }
            else -> {
                ""
            }
        }
        return error.isEmpty()
    }

    private fun init() {
        onFocusChangeListener = this
    }
}