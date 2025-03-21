package com.example.goalguru.ui.theme

import UserViewModel
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goalguru.R
import com.example.goalguru.base.MyApplication
import com.example.goalguru.model.Comment
import com.example.goalguru.model.Model
import com.example.goalguru.model.Post
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PostAdapter(
    private var posts: MutableList<Post> = mutableListOf(),
    private val postsFragment: PostsFragment,
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private val userViewModel = UserViewModel()

    fun set(posts: MutableList<Post>) {
        userViewModel.userUid.observe(viewLifecycleOwner) { redUid ->
            val filteredPosts = if (postsFragment.getPostType() == "your_posts") {
                posts.filter { it.userId == redUid }
            } else {
                posts.filter { it.userId != redUid }
            }.sortedByDescending { it.timestamp }

            this.posts = filteredPosts.toMutableList()
            notifyDataSetChanged()
        }
    }

    fun editPost(post: Post, position: Int) {
        posts[position] = post
        notifyItemChanged(position)
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

        if (post.userProfilePicture.isNotEmpty()) {
            Glide.with(context)
                .load(post.userProfilePicture)
                .error(R.drawable.ic_launcher_foreground)
                .circleCrop()
                .into(holder.profilePhoto)
        } else {
            holder.profilePhoto.setImageResource(R.drawable.default_profile)
        }
        Log.d("post: ", post.toString())
        holder.userName.text = post.username
        holder.postText.text = post.text

        // Format and set the timestamp
        val formattedDate = formatTimestamp(post.timestamp ?: 0)
        holder.publishDate.text = formattedDate

        // Show or hide edit/delete buttons based on whether current user is the post creator
        if (post.userId == Model.shared.getCurrentUserId()) {
            holder.postActionsContainer.visibility = View.VISIBLE

            // Set up edit button click listener
            holder.btnEditPost.setOnClickListener {
                onEditClick(post, position)
            }

            // Set up delete button click listener
            holder.btnDeletePost.setOnClickListener {
                onDeleteClick(post, holder, position)
            }
        } else {
            holder.postActionsContainer.visibility = View.GONE
        }

        updateLikeCount(holder, post)

        setupLikeButton(holder, post, position)

        setupPostImages(holder, post)

        // Set up comments recycler view
        holder.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        val commentAdapter = CommentAdapter(post.comments)
        holder.commentsRecyclerView.adapter = commentAdapter

        // Set up comments section
        holder.commentAction.setOnClickListener {
            toggleCommentsSection(holder, post)
        }
        // Set up comments count
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
                // Create and add the new comment
                val newComment = Comment(
                    userId = Model.shared.getCurrentUserId(),
                    username = Model.shared.getCurrentUserUsername(),
                    text = commentText,
                    id = UUID.randomUUID().toString(),
                    postId = post.id,
                    timestamp = System.currentTimeMillis(),
                    userProfilePicture = Model.shared.getCurrentUserImage()
                )
                Log.d("comment", "comment: $newComment")
                // Save the comment to the database
                Model.shared.addComment(newComment) { success ->
                    if (success) {
                        post.comments.add(newComment)
                        commentAdapter.notifyItemInserted(post.comments.size - 1)
                        holder.commentInput.text.clear()
                    } else {
                        Toast.makeText(MyApplication.Globals.context, "Failed to add comment", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun onDeleteClick(post: Post, holder: PostViewHolder, position: Int) {
        val context = holder.itemView.context

        AlertDialog.Builder(context)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->

                posts.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, posts.size)
                Model.shared.deletePost(post.id) { success ->
                    if (!success) {
                        // Revert if server deletion fails
                        posts.add(position, post)
                        notifyItemInserted(position)
                        Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun onEditClick(post: Post, position: Int) {
        postsFragment.editPost(post, position)
    }

    private fun toggleCommentsSection(holder: PostViewHolder, post: Post) {
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
        if (post.isLikedByUser) {
            holder.likeIcon.setImageResource(R.drawable.ic_like_filled)
        } else {
            holder.likeIcon.setImageResource(R.drawable.ic_like)
        }

        // Set click listener for like button
        holder.likeAction.setOnClickListener {
            toggleLike(post, holder, position)
            // Update the button appearance immediately
            setupLikeButton(holder, post, position)
        }
    }

    private fun toggleLike(post: Post, holder: PostViewHolder, position: Int) {
        if (post.isLikedByUser) {
            post.isLikedByUser = false
            post.likesCount = (post.likesCount - 1).coerceAtLeast(0)
        } else {
            post.isLikedByUser = true
            post.likesCount++
        }

        // Update the like icon and like count immediately
        if (post.isLikedByUser) {
            holder.likeIcon.setImageResource(R.drawable.ic_like_filled)
        } else {
            holder.likeIcon.setImageResource(R.drawable.ic_like)
        }
        updateLikeCount(holder, post)

        Model.shared.toggleLike(post.id) { success ->
            if (!success) {
                // Revert the like state if the operation failed
                if (post.isLikedByUser) {
                    post.isLikedByUser = false
                    post.likesCount = (post.likesCount - 1).coerceAtLeast(0)
                } else {
                    post.isLikedByUser = true
                    post.likesCount++
                }
                // Update the like icon and like count again
                if (post.isLikedByUser) {
                    holder.likeIcon.setImageResource(R.drawable.ic_like_filled)
                } else {
                    holder.likeIcon.setImageResource(R.drawable.ic_like)
                }
                updateLikeCount(holder, post)
                Toast.makeText(MyApplication.Globals.context, "Failed to toggle like", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLikeCount(holder: PostViewHolder, post: Post) {
        // Format like count text based on number of likes
        holder.likesCount.text = when (post.likesCount) {
            0 -> "No likes yet"
            1 -> "1 like"
            else -> "${post.likesCount} likes"
        }
    }

    override fun getItemCount(): Int = posts.size
}