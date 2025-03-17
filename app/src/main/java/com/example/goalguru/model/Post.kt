package com.example.goalguru.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.goalguru.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.UUID

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val text: String,
    val imageUrls: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class Post(
    val id: String,
    val userId: String,
    var text: String,
    val imageUrls: List<String>,
    var likesCount: Int = 0,
    var isLikedByUser: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf(),
    val username: String = "",
    val userProfilePicture: String = "",
    val timestamp: Long? = null,
) {

    companion object {
        var lastUpdated: Long
            get() {
                return MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                val sharedPreferences = MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                sharedPreferences?.edit()?.putLong(LOCAL_LAST_UPDATED, value)?.apply()
            }

        const val KEY_ID = "id"
        const val KEY_USER_ID = "userId"
        const val KEY_USERNAME = "username"
        const val KEY_TEXT = "text"
        const val KEY_IMAGE_URLS = "imageUrls"
        const val KEY_LIKES = "likes"
        const val KEY_LIKED_BY_USER = "likedByUser"
        const val KEY_COMMENTS = "comments"
        const val KEY_TIMESTAMP = "timestamp"
        const val LAST_UPDATED = "lastUpdated"
        private const val LOCAL_LAST_UPDATED = "posts_last_updated"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[KEY_ID] as UUID
            val userId = json[KEY_USER_ID] as UUID
            val username = json[KEY_USERNAME] as String
            val text = json[KEY_TEXT] as String
            val imageUrls = json[KEY_IMAGE_URLS] as List<String>
            val likes = (json[KEY_LIKES] as Long).toInt()
            val likedByUser = json[KEY_LIKED_BY_USER] as Boolean
            val comments = json[KEY_COMMENTS] as MutableList<Comment>
            val timestamp = json[KEY_TIMESTAMP] as Long

            return Post(
                id = id.toString(),
                userId = userId.toString(),
                username = username,
                text = text,
                imageUrls = imageUrls,
                likesCount = likes,
                isLikedByUser = likedByUser,
                comments = comments,
                timestamp = timestamp,
            )
        }
    }

    val json: HashMap<String, Any?>
        get() = hashMapOf(
            KEY_ID to id,
            KEY_USER_ID to userId,
            KEY_USERNAME to username,
            KEY_TEXT to text,
            KEY_IMAGE_URLS to imageUrls,
            KEY_LIKES to likesCount,
            KEY_LIKED_BY_USER to isLikedByUser,
            KEY_COMMENTS to comments,
            KEY_TIMESTAMP to timestamp,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
}
