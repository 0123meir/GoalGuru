package com.example.goalguru.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.R

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username)
        val postText: TextView = itemView.findViewById(R.id.post_description)
        val likesCount: TextView = itemView.findViewById(R.id.like_count)
        val likeButton: Button = itemView.findViewById(R.id.like_button)
        val commentButton: Button = itemView.findViewById(R.id.comment_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.userName.text = post.userName
        holder.postText.text = post.text
        holder.likesCount.text = "${post.likes} likes"

        holder.likeButton.setOnClickListener {
            post.likes++
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = posts.size
}