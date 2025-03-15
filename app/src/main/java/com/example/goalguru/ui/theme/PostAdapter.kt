package com.example.goalguru.ui.theme

import android.app.AlertDialog
import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goalguru.R
import com.example.goalguru.model.Comment
import com.example.goalguru.model.Post
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(
    private var posts: List<Post>,
    private val currentUserId: String = "user_id_placeholder" // This would come from your auth system
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    fun set(posts: List<Post>) {
        this.posts = posts
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhoto: ImageView = itemView.findViewById(R.id.iv_profile_photo)
        val userName: TextView = itemView.findViewById(R.id.username)
        val publishDate: TextView = itemView.findViewById(R.id.publish_date)
        val postText: TextView = itemView.findViewById(R.id.post_description)
        val likesCount: TextView = itemView.findViewById(R.id.tv_likes_count)
        val commentsCount: TextView = itemView.findViewById(R.id.tv_comments_count)
        val likeAction: LinearLayout = itemView.findViewById(R.id.action_like)
        val commentAction: LinearLayout = itemView.findViewById(R.id.action_comment)
        val likeIcon: ImageView = itemView.findViewById(R.id.like)

        // comments section
        val commentsSection: LinearLayout = itemView.findViewById(R.id.comments_section)
        val commentsRecyclerView: RecyclerView = itemView.findViewById(R.id.comments_recycler_view)
        val commentInput: EditText = itemView.findViewById(R.id.comment_input)
        val submitCommentButton: MaterialButton = itemView.findViewById(R.id.submit_comment_button)

        // Image views
        val image1: ImageView = itemView.findViewById(R.id.image_1)
        val image2: ImageView = itemView.findViewById(R.id.image_2)
        val image3: ImageView = itemView.findViewById(R.id.image_3)
        val image1Container: MaterialCardView = itemView.findViewById(R.id.image_1_container)
        val image2Container: MaterialCardView = itemView.findViewById(R.id.image_2_container)
        val image3Container: MaterialCardView = itemView.findViewById(R.id.image_3_container)

        val postActionsContainer: LinearLayout = itemView.findViewById(R.id.post_actions_container)
        val btnEditPost: ImageButton = itemView.findViewById(R.id.btn_edit_post)
        val btnDeletePost: ImageButton = itemView.findViewById(R.id.btn_delete_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context

        if (!post.userProfile.isNullOrEmpty()) {
            Glide.with(context)
                .load(post.userProfile)
                .error(R.drawable.ic_launcher_foreground)
                .circleCrop()
                .into(holder.profilePhoto)
        } else {
            holder.profilePhoto.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.userName.text = post.userName
        holder.postText.text = post.text

        // Format and set the timestamp
        val formattedDate = formatTimestamp(post.timestamp)
        holder.publishDate.text = formattedDate

        // Show or hide edit/delete buttons based on whether current user is the post creator
        if (true) { // TODO: change to post.userId == currentUserId
            holder.postActionsContainer.visibility = View.VISIBLE

            // Set up edit button click listener
            holder.btnEditPost.setOnClickListener {
                onEditClick(post,holder, position)
            }

            // Set up delete button click listener
            holder.btnDeletePost.setOnClickListener {
                onDeleteClick(post, holder, position)
            }
        } else {
            holder.postActionsContainer.visibility = View.GONE
        }

        updateLikeCount(holder, post)

        // Set up like button state and click listener
        setupLikeButton(holder, post, position)

        // Set up images based on how many the post has
        setupPostImages(holder, post)

        // Set up comments recycler view
        holder.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        val commentAdapter = CommentAdapter(post.comments)
        holder.commentsRecyclerView.adapter = commentAdapter

        // Set up comments section
        holder.commentAction.setOnClickListener {
            toggleCommentsSection(holder, post)
        }
        //set up comments count
        holder.commentsCount.text = "${post.comments.size}"

        // Enable/disable submit comment button based on input
        holder.commentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                holder.submitCommentButton.isEnabled = !s.isNullOrBlank()
            }
        })

        // Initialize submit button state
        holder.submitCommentButton.isEnabled = !holder.commentInput.text.isNullOrBlank()

        // Set up submit comment button
        holder.submitCommentButton.setOnClickListener {
            val commentText = holder.commentInput.text.toString().trim()

            if (commentText.isNotEmpty()) {
                val currentUserName = "Current User" // Replace with actual username when DB is ready

                // Create and add the new comment
                val newComment = Comment(
                    userId = currentUserId,
                    userName = currentUserName,
                    text = commentText
                )

                // Add comment to post's comments list
                post.comments.add(newComment)

                // TODO: save the comment to the database here

                // Update the UI
                holder.commentInput.text.clear()
                commentAdapter.notifyItemInserted(post.comments.size - 1)
            }
        }
    }

    private fun onDeleteClick(post: Post, holder: PostViewHolder, position: Int) {
        val context = holder.itemView.context

        AlertDialog.Builder(context)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                // Remove post from list
                val mutablePosts = posts.toMutableList()
                mutablePosts.removeAt(position)
                posts = mutablePosts

                // TODO: Delete post from database

                notifyItemRemoved(position)
                Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onEditClick(post: Post, holder : PostAdapter.PostViewHolder, position: Int) {

            // Get the Dialog
            val dialog = Dialog(holder.itemView.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_edit_post)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            // Get views from dialog
            val etEditPostText: TextInputEditText = dialog.findViewById(R.id.et_edit_post_text)
            val btnCancelEdit: MaterialButton = dialog.findViewById(R.id.btn_cancel_edit)
            val btnSaveEdit: MaterialButton = dialog.findViewById(R.id.btn_save_edit)

            // Set up image previews
            val editImagePreview1: ImageView = dialog.findViewById(R.id.edit_image_preview_1)
            val editImagePreview2: ImageView = dialog.findViewById(R.id.edit_image_preview_2)
            val editImagePreview3: ImageView = dialog.findViewById(R.id.edit_image_preview_3)

            // Populate form with existing post data
            etEditPostText.setText(post.text)

            // Show existing images if any
            if (post.imageUrls.isNotEmpty()) {
                editImagePreview1.visibility = View.VISIBLE
                Glide.with(dialog.context)
                    .load(post.imageUrls[0])
                    .error(R.drawable.ic_launcher_foreground)
                    .into(editImagePreview1)
            }

            if (post.imageUrls.size > 1) {
                editImagePreview2.visibility = View.VISIBLE
                Glide.with(dialog.context)
                    .load(post.imageUrls[1])
                    .error(R.drawable.ic_launcher_foreground)
                    .into(editImagePreview2)
            }

            if (post.imageUrls.size > 2) {
                editImagePreview3.visibility = View.VISIBLE
                Glide.with(dialog.context)
                    .load(post.imageUrls[2])
                    .error(R.drawable.ic_launcher_foreground)
                    .into(editImagePreview3)
            }

            // Set up button listeners
            btnCancelEdit.setOnClickListener {
                dialog.dismiss()
            }

            btnSaveEdit.setOnClickListener {
                val updatedText = etEditPostText.text.toString().trim()

                if (updatedText.isEmpty()) {
                    Toast.makeText(dialog.context, "post text cannot be empty", Toast.LENGTH_SHORT).show()
                }
                if(updatedText.length > 200) {
                    Toast.makeText(dialog.context, "text length must be under 200 characters",Toast.LENGTH_SHORT).show()

                } else {
                    post.text = updatedText

                    // TODO: Update post in database

                    notifyItemChanged(position)
                    dialog.dismiss()
                    Toast.makeText(dialog.context, "Post updated successfully!", Toast.LENGTH_SHORT).show()

                }
            }

            dialog.show()
        }


    private fun toggleCommentsSection(holder: PostAdapter.PostViewHolder, post: Post) {
        if(holder.commentsSection.visibility == View.VISIBLE){
            holder.commentsSection.visibility = View.GONE
        }else{
            holder.commentsSection.visibility = View.VISIBLE
        }
    }

    private fun setupPostImages(holder: PostViewHolder, post: Post) {
        // Hide all images by default
        holder.image1Container.visibility = View.GONE
        holder.image2Container.visibility = View.GONE
        holder.image3Container.visibility = View.GONE
        val context = holder.itemView.context

        when (post.imageUrls.size) {
            0 -> {
                // No images to show, keep all hidden
            }
            1 -> {
                // Show only the first image
                holder.image1Container.visibility = View.VISIBLE
                Glide.with(context).load(post.imageUrls[0]).error(R.drawable.ic_launcher_foreground).into(holder.image1)

            }
            2 -> {
                // Show first two images
                holder.image1Container.visibility = View.VISIBLE
                holder.image2Container.visibility = View.VISIBLE
                Glide.with(context).load(post.imageUrls[0]).error(R.drawable.ic_launcher_foreground).into(holder.image1)
                Glide.with(context).load(post.imageUrls[1]).error(R.drawable.ic_launcher_foreground).into(holder.image2)

            }
            else -> {
                // Show all three images
                holder.image1Container.visibility = View.VISIBLE
                holder.image2Container.visibility = View.VISIBLE
                holder.image3Container.visibility = View.VISIBLE
                Glide.with(context).load(post.imageUrls[0]).error(R.drawable.ic_launcher_foreground).into(holder.image1)
                Glide.with(context).load(post.imageUrls[1]).error(R.drawable.ic_launcher_foreground).into(holder.image2)
                Glide.with(context).load(post.imageUrls[2]).error(R.drawable.ic_launcher_foreground).into(holder.image3)
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun setupLikeButton(holder: PostViewHolder, post: Post, position: Int) {
        val context = holder.itemView.context

        // Update button appearance based on like state
        if (post.likedByUser) {
            holder.likeIcon.setImageResource(R.drawable.ic_like_filled)
        } else {
            holder.likeIcon.setImageResource(R.drawable.ic_like)
        }

        // Set click listener for like button
        holder.likeAction.setOnClickListener {
            toggleLike(post, position)
            // Update the button appearance immediately
            setupLikeButton(holder, post, position)
        }
    }

    private fun toggleLike(post: Post, position: Int) {

        if (post.likedByUser) {
            post.likedByUser = false
            post.likes = (post.likes - 1).coerceAtLeast(0)
        } else {
            post.likedByUser = true
            post.likes++
        }

        // TODO: Update like status in the database here

        // Notify adapter that this item has changed
        notifyItemChanged(position)
    }

    private fun updateLikeCount(holder: PostAdapter.PostViewHolder, post: Post) {
        // Format like count text based on number of likes
        holder.likesCount.text = when (post.likes) {
            0 -> "No likes yet"
            1 -> "1 like"
            else -> "${post.likes} likes"
        }
    }

    override fun getItemCount() = posts.size
}