package com.rockytauhid.storyapps.view.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.rockytauhid.storyapps.R

class EmailEditText : TextInputEditText, View.OnFocusChangeListener {

    companion object {
        private val emailRegex: Regex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
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
        hint = resources.getString(R.string.enter_email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus && !text.isNullOrEmpty() && !emailRegex.matches(text.toString()))
            error = context.getString(R.string.invalid_email)
    }

    private fun init() {
        onFocusChangeListener = this
    }
}