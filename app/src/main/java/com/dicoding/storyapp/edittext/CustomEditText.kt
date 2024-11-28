package com.dicoding.storyapp.edittext

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(text, start, before, count)

        // Validasi jika password kurang dari 8 karakter
        if (text != null && text.length < 8) {
            error = context.getString(R.string.password_too_short)
        }
    }
}
