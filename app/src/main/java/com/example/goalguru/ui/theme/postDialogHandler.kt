package com.example.goalguru.ui.theme

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.example.goalguru.R
import com.example.goalguru.model.Post
import com.google.android.material.card.MaterialCardView

class PostDialogHandler(private val context: Context) {

    interface PostDialogCallback {
        fun onPostSubmitted(text: String, imageUris: List<String>)
    }

    private var dialog: Dialog? = null
    private val selectedImageUris = mutableListOf<String>()

    fun showCreatePostDialog(
        imageContentLauncher: ActivityResultLauncher<String>,
        callback: PostDialogCallback
    ) {
        // Reset state for new dialog
        selectedImageUris.clear()
        setupDialog("Create Post", null, imageContentLauncher, callback)
    }

    fun showEditPostDialog(
        post: Post,
        imageContentLauncher: ActivityResultLauncher<String>?,
        callback: PostDialogCallback
    ) {
        Log.d("TAG", (post.imageUrls === selectedImageUris).toString())
        // Reset and populate with existing data
        selectedImageUris.clear()
        selectedImageUris.addAll(post.imageUrls)
        setupDialog("Edit Post", post, imageContentLauncher, callback)
    }

    private fun setupDialog(
        title: String,
        existingPost: Post?,
        imageContentLauncher: ActivityResultLauncher<String>?,
        callback: PostDialogCallback
    ) {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_create_edit_post)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val dialogTitle: TextView = dialog?.findViewById(R.id.dialog_title)!!
        val etPostText: EditText = dialog?.findViewById(R.id.et_post_text)!!
        val btnAddImage: Button = dialog?.findViewById(R.id.btn_add_image)!!
        val btnCancel: Button = dialog?.findViewById(R.id.btn_cancel)!!
        val btnSubmit: Button = dialog?.findViewById(R.id.btn_submit)!!

        val btnRemoveImage1: ImageView = dialog?.findViewById(R.id.delete_image_1)!!
        val btnRemoveImage2: ImageView = dialog?.findViewById(R.id.delete_image_2)!!
        val btnRemoveImage3: ImageView = dialog?.findViewById(R.id.delete_image_3)!!

        // Set the texts based on create/edit mode
        dialogTitle.text = title
        if(title == "Create Post") {
            btnSubmit.text = "Post"
        } else {
            btnSubmit.text = "Update"
            etPostText.setText(existingPost?.text)
        }

        btnRemoveImage1.setOnClickListener {
            selectedImageUris.removeAt(0)
            updateImagePreviews(selectedImageUris)
        }
        btnRemoveImage2.setOnClickListener {
            selectedImageUris.removeAt(1)
            updateImagePreviews(selectedImageUris)
        }
        btnRemoveImage3.setOnClickListener {
            selectedImageUris.removeAt(2)
            updateImagePreviews(selectedImageUris)
        }

        btnAddImage.setOnClickListener {
            if (selectedImageUris.size < 3) {
                imageContentLauncher?.launch("image/*")
            } else {
                Toast.makeText(context, "Maximum 3 images allowed", Toast.LENGTH_SHORT).show()
            }
        }


        // Set up cancel button
        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        // Set up submit button
        btnSubmit.setOnClickListener {
            val postText = etPostText.text.toString().trim()

            when {
                postText.isEmpty() -> {
                    Toast.makeText(context, "Please enter text for your post", Toast.LENGTH_SHORT).show()
                }
                postText.length > 200 -> {
                    Toast.makeText(context, "Text length must be under 200 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    callback.onPostSubmitted(postText, selectedImageUris.toList())
                    dialog?.dismiss()
                }
            }
        }

        dialog?.show()

        // hide and show images
        updateImagePreviews(selectedImageUris)
    }

    fun addImage(uri: Uri) {
        selectedImageUris.add(uri.toString())
        updateImagePreviews(selectedImageUris)
    }

    private fun updateImagePreviews(imageUris: List<String>) {
        if (dialog != null && dialog?.isShowing == true) {
            // Get image preview containers and views
            val imagePreviewContainer1: MaterialCardView = dialog?.findViewById(R.id.image_preview_container_1)!!
            val imagePreviewContainer2: MaterialCardView = dialog?.findViewById(R.id.image_preview_container_2)!!
            val imagePreviewContainer3: MaterialCardView = dialog?.findViewById(R.id.image_preview_container_3)!!

            val imagePreview1: ImageView = dialog?.findViewById(R.id.image_preview_1)!!
            val imagePreview2: ImageView = dialog?.findViewById(R.id.image_preview_2)!!
            val imagePreview3: ImageView = dialog?.findViewById(R.id.image_preview_3)!!

            // Reset visibility
            imagePreviewContainer1.visibility = View.GONE
            imagePreviewContainer2.visibility = View.GONE
            imagePreviewContainer3.visibility = View.GONE

            // Update visibility and content based on selected images
            if (imageUris.isNotEmpty()) {
                imagePreviewContainer1.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imageUris[0])
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imagePreview1)
            }

            if (imageUris.size > 1) {
                imagePreviewContainer2.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imageUris[1])
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imagePreview2)
            }

            if (imageUris.size > 2) {
                imagePreviewContainer3.visibility = View.VISIBLE
                Glide.with(context)
                    .load(imageUris[2])
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imagePreview3)
            }
        }
    }
}