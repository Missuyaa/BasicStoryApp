package com.dicoding.storyapp.edittext

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        // Tambahkan listener untuk validasi saat fokus berubah
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Validasi hanya saat input kehilangan fokus
                validatePassword()
            }
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(text, start, before, count)

        // Opsional: Tambahkan validasi langsung saat pengguna mengetik
        // Anda bisa memilih untuk mematikan validasi langsung ini jika tidak diperlukan
        if (text != null && text.length < 8) {
            error = context.getString(R.string.password_too_short)
        } else {
            error = null // Hapus pesan error jika password valid
        }
    }

    // Fungsi untuk validasi password
    private fun validatePassword() {
        val text = text?.toString() ?: ""
        if (text.length < 8) {
            error = context.getString(R.string.password_too_short)
        } else {
            error = null // Hapus pesan error jika password valid
        }
    }
}
