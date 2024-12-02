package com.dicoding.storyapp.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R

class AddStoryActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            val imageView = findViewById<ImageView>(R.id.iv_add_story_photo)
            if (uri != null) {
                Glide.with(this).load(uri).into(imageView)
            } else {
                Toast.makeText(this, "Gagal memilih gambar", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        val btnChoosePhoto = findViewById<Button>(R.id.btn_choose_photo)
        val btnUploadStory = findViewById<Button>(R.id.btn_upload_story)
        val edtDescription = findViewById<EditText>(R.id.ed_story_description)

        btnChoosePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnUploadStory.setOnClickListener {
            val description = edtDescription.text.toString()

            if (selectedImageUri == null) {
                Toast.makeText(this, "Silakan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (description.isBlank()) {
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadStory(description, selectedImageUri!!)
        }
    }

    private fun uploadStory(description: String, imageUri: Uri) {
        Toast.makeText(this, "Cerita berhasil diunggah!", Toast.LENGTH_SHORT).show()
        finish()
    }
}

