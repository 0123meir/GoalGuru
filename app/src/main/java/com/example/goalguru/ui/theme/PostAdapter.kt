package com.example.goalguru.ui.theme

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goalguru.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(
    private val posts: List<Post>,
    private val currentUserId: String = "user_id_placeholder" // This would come from your auth system
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhoto: ImageView = itemView.findViewById(R.id.iv_profile_photo)
        val userName: TextView = itemView.findViewById(R.id.username)
        val publishDate: TextView = itemView.findViewById(R.id.publish_date)
        val postText: TextView = itemView.findViewById(R.id.post_description)
        val likesCount: TextView = itemView.findViewById(R.id.like_count)
        val likeButton: Button = itemView.findViewById(R.id.like_button)
        val commentButton: Button = itemView.findViewById(R.id.comment_button)
        val commentInput: EditText = itemView.findViewById(R.id.comment_input)
        val submitCommentButton: Button = itemView.findViewById(R.id.submit_comment_button)
        val commentsRecyclerView: RecyclerView = itemView.findViewById(R.id.comments_recycler_view)

        // Image views
        val image1: ImageView = itemView.findViewById(R.id.image_1)
        val image2: ImageView = itemView.findViewById(R.id.image_2)
        val image3: ImageView = itemView.findViewById(R.id.image_3)
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

        updateLikeCount(holder, post)

        // Set up like button state and click listener
        setupLikeButton(holder, post, position)

        // Set up images based on how many the post has
        setupPostImages(holder, post)

        // Set up comments recycler view
        holder.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        val commentAdapter = CommentAdapter(post.comments)
        holder.commentsRecyclerView.adapter = commentAdapter

        // Set up comment button to focus on input
        holder.commentButton.setOnClickListener {
            holder.commentInput.requestFocus()
        }

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

    private fun setupPostImages(holder: PostViewHolder, post: Post) {
        // Hide all images by default
        holder.image1.visibility = View.GONE
        holder.image2.visibility = View.GONE
        holder.image3.visibility = View.GONE
        val context = holder.itemView.context

        when (post.imageUrls.size) {
            0 -> {
                // No images to show, keep all hidden
            }
            1 -> {
                // Show only the first image
                holder.image1.visibility = View.VISIBLE
                Glide.with(context).load(post.imageUrls[0]).error(R.drawable.ic_launcher_foreground).into(holder.image1)

                // Set layout parameters for single image view (make it wider)
                val params = holder.image1.layoutParams as LinearLayout.LayoutParams
                params.weight = 1f
                params.width = 0 // Let weight handle the sizing
                holder.image1.layoutParams = params
            }
            2 -> {
                // Show first two images
                holder.image1.visibility = View.VISIBLE
                holder.image2.visibility = View.VISIBLE
                Glide.with(context).load(post.imageUrls[0]).error(R.drawable.ic_launcher_foreground).into(holder.image1)
                Glide.with(context).load(post.imageUrls[1]).error(R.drawable.ic_launcher_foreground).into(holder.image2)

                // Set layout parameters
                val params1 = holder.image1.layoutParams as LinearLayout.LayoutParams
                val params2 = holder.image2.layoutParams as LinearLayout.LayoutParams
                params1.weight = 1f
                params2.weight = 1f
                params1.width = 0 // Let weight handle the sizing
                params2.width = 0 // Let weight handle the sizing
                holder.image1.layoutParams = params1
                holder.image2.layoutParams = params2
            }
            else -> {
                // Show all three images
                holder.image1.visibility = View.VISIBLE
                holder.image2.visibility = View.VISIBLE
                holder.image3.visibility = View.VISIBLE
                Glide.with(context).load(post.imageUrls[0]).error(R.drawable.ic_launcher_foreground).into(holder.image1)
                Glide.with(context).load(post.imageUrls[1]).error(R.drawable.ic_launcher_foreground).into(holder.image2)
                Glide.with(context).load(post.imageUrls[2]).error(R.drawable.ic_launcher_foreground).into(holder.image3)

                // Set layout parameters
                val params1 = holder.image1.layoutParams as LinearLayout.LayoutParams
                val params2 = holder.image2.layoutParams as LinearLayout.LayoutParams
                val params3 = holder.image3.layoutParams as LinearLayout.LayoutParams
                params1.weight = 1f
                params2.weight = 1f
                params3.weight = 1f
                params1.width = 0 // Let weight handle the sizing
                params2.width = 0 // Let weight handle the sizing
                params3.width = 0 // Let weight handle the sizing
                holder.image1.layoutParams = params1
                holder.image2.layoutParams = params2
                holder.image3.layoutParams = params3
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
            holder.likeButton.text = "Liked"

            val likedColor = ContextCompat.getColor(context, R.color.liked_button_color)
            holder.likeButton.setTextColor(likedColor)

        } else {
            holder.likeButton.text = "Like"
            holder.likeButton.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }

        // Set click listener for like button
        holder.likeButton.setOnClickListener {
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

    private fun updateLikeCount(holder: PostViewHolder, post: Post) {
        // Format like count text based on number of likes
        holder.likesCount.text = when (post.likes) {
            0 -> "No likes yet"
            1 -> "1 like"
            else -> "${post.likes} likes"
        }
    }

    override fun getItemCount() = posts.size
}