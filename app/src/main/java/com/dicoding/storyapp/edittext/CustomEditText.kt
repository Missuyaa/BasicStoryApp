package com.dicoding.storyapp.edittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    // Callback untuk mengirim status validasi ke luar
    var onValidationChanged: ((Boolean) -> Unit)? = null

    init {
        // Tambahkan listener untuk memantau perubahan teks
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Fungsi untuk memvalidasi password
    private fun validatePassword(input: String) {
        if (input.length < 8) {
            error = context.getString(R.string.password_too_short) // Pastikan resource string tersedia
        } else {
            error = null
        }
    }

}
