package com.example.goalguru.model

import androidx.room.*

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val postId: String,
    val timestamp: Long = System.currentTimeMillis()
) {

    companion object {
        const val KEY_ID = "id"
        const val KEY_USER_ID = "userId"
        const val KEY_POST_ID = "postId"
        const val KEY_TIMESTAMP = "timestamp"

        fun fromJSON(json: Map<String, Any>): LikeEntity {
            val id = json[KEY_ID] as String
            val userId = json[KEY_USER_ID] as String
            val postId = json[KEY_POST_ID] as String
            val timestamp = json[KEY_TIMESTAMP] as Long

            return LikeEntity(
                id = id,
                userId = userId,
                postId = postId,
                timestamp = timestamp
            )
        }
    }

    val json: HashMap<String, Any?>
        get() = hashMapOf(
            KEY_ID to id,
            KEY_USER_ID to userId,
            KEY_POST_ID to postId,
            KEY_TIMESTAMP to timestamp
        )
}