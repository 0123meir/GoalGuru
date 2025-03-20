package com.example.goalguru.ui.theme

import android.app.Dialog
import android.content.Context
import android.net.Uri
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
import com.example.goalguru.model.Model
import com.example.goalguru.model.Post
import com.google.android.material.card.MaterialCardView
import java.util.UUID

class PostDialogHandler(private val context: Context) {

    interface PostDialogCallback {
        fun onPostSubmitted(post: Post)
    }

    private var dialog: Dialog? = null
    private val selectedImageUris = mutableListOf<String>()

    fun showPostDialog(
        post: Post?,
        imageContentLauncher: ActivityResultLauncher<String>,
        callback: PostDialogCallback
    ) {
        selectedImageUris.clear()
        post?.imageUrls?.let { selectedImageUris.addAll(it) }
        setupDialog(post, imageContentLauncher, callback)
    }

    private fun setupDialog(
        post: Post?,
        imageContentLauncher: ActivityResultLauncher<String>,
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

        etPostText.setText(post?.text ?: "")
        btnSubmit.text = if (post == null) "Post" else "Update"
        dialogTitle.text = if (post == null) "Create New Post" else "Edit Post"
        
        btnRemoveImage1.setOnClickListener { removeImageAt(0) }
        btnRemoveImage2.setOnClickListener { removeImageAt(1) }
        btnRemoveImage3.setOnClickListener { removeImageAt(2) }

        btnAddImage.setOnClickListener {
            if (selectedImageUris.size < 3) {
                imageContentLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Maximum 3 images allowed", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener { dialog?.dismiss() }

        btnSubmit.setOnClickListener {
            val postText = etPostText.text.toString().trim()
            if (postText.isEmpty()) {
                Toast.makeText(context, "Please enter text for your post", Toast.LENGTH_SHORT).show()
            } else if (postText.length > 200) {
                Toast.makeText(context, "Text length must be under 200 characters", Toast.LENGTH_SHORT).show()
            } else {
                val newPost = post?.copy(text = postText, imageUrls = selectedImageUris.toList()) ?: Post(
                    id = UUID.randomUUID().toString(),
                    userId = Model.shared.getCurrentUserId(),
                    text = postText,
                    imageUrls = selectedImageUris.toList(),
                    timestamp = System.currentTimeMillis(),
                    likesCount = 0,
                    isLikedByUser = false,
                    comments = mutableListOf(),
                    username = Model.shared.getCurrentUserUsername(),
                    userProfilePicture = Model.shared.getCurrentUserImage()
                )
                callback.onPostSubmitted(newPost)
                dialog?.dismiss()
            }
        }

        dialog?.show()
        updateImagePreviews()
    }

    private fun removeImageAt(index: Int) {
        if (index < selectedImageUris.size) {
            selectedImageUris.removeAt(index)
            updateImagePreviews()
        }
    }

    fun addImage(uri: Uri) {
        selectedImageUris.add(uri.toString())
        updateImagePreviews()
    }

    private fun updateImagePreviews() {
        if (dialog != null && dialog?.isShowing == true) {
            val imagePreviewContainer1: MaterialCardView = dialog?.findViewById(R.id.image_preview_container_1)!!
            val imagePreviewContainer2: MaterialCardView = dialog?.findViewById(R.id.image_preview_container_2)!!
            val imagePreviewContainer3: MaterialCardView = dialog?.findViewById(R.id.image_preview_container_3)!!

            val imagePreview1: ImageView = dialog?.findViewById(R.id.image_preview_1)!!
            val imagePreview2: ImageView = dialog?.findViewById(R.id.image_preview_2)!!
            val imagePreview3: ImageView = dialog?.findViewById(R.id.image_preview_3)!!

            imagePreviewContainer1.visibility = View.GONE
            imagePreviewContainer2.visibility = View.GONE
            imagePreviewContainer3.visibility = View.GONE

            if (selectedImageUris.isNotEmpty()) {
                imagePreviewContainer1.visibility = View.VISIBLE
                Glide.with(context).load(selectedImageUris[0]).error(R.drawable.ic_launcher_foreground).into(imagePreview1)
            }
            if (selectedImageUris.size > 1) {
                imagePreviewContainer2.visibility = View.VISIBLE
                Glide.with(context).load(selectedImageUris[1]).error(R.drawable.ic_launcher_foreground).into(imagePreview2)
            }
            if (selectedImageUris.size > 2) {
                imagePreviewContainer3.visibility = View.VISIBLE
                Glide.with(context).load(selectedImageUris[2]).error(R.drawable.ic_launcher_foreground).into(imagePreview3)
            }
        }
    }
}