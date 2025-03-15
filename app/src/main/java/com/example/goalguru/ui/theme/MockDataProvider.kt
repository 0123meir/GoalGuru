package com.example.goalguru.util

import com.example.goalguru.model.Comment
import com.example.goalguru.model.Post
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Utility class to generate mock data for testing and development
 */
object MockDataProvider {

    private val userNames = listOf(
        "john_fitness", "sarah_runner", "mike_gym",
        "fitness_lover", "health_guru", "yoga_master",
        "weight_lifter", "marathon_runner", "cross_fit_pro"
    )

    private val postTexts = listOf(
        "Just completed my 10km run. Feeling great!",
        "New personal best at the gym today. 💪",
        "Morning yoga session - perfect way to start the day.",
        "Taking a rest day today. Remember recovery is just as important as training!",
        "My meal prep for the week is done. Eating healthy saves time and money.",
        "Joined a new fitness class today. Can't wait for the next session!",
        "Hit my weight loss goal today! Hard work pays off.",
        "Mountain hiking on the weekend - who's in?",
        "Day 30 of my fitness challenge completed. Consistency is key!",
        "Learning new exercises for my back. Always improving!"
    )

    private val commentTexts = listOf(
        "Great job! Keep it up!",
        "Looking strong!",
        "That's inspiring",
        "Love your dedication",
        "Can you share your routine?",
        "Awesome progress",
        "Way to go!",
        "What's your diet like?",
        "I need to try this",
        "You're crushing it!",
        "This is motivational",
        "How often do you train?",
        "Amazing results",
        "Any tips for beginners?",
        "Goals!"
    )

    /**
     * Generates a list of mock posts with comments
     *
     * @param count The number of posts to generate
     * @return List of Post objects with random data
     */
    fun generateMockPosts(count: Int = 10): MutableList<Post> {
        val currentTime = System.currentTimeMillis()

        return MutableList(count) { postIndex ->
            val postId = UUID.randomUUID().toString()
            val userId = "user_${UUID.randomUUID().toString().substring(0, 6)}"
            val userName = userNames.random()
            val postText = postTexts[postIndex % postTexts.size]

            // Generate between 0-5 images (for future use)
            val imageCount = (0..3).random()
            val imageUrls = List(imageCount) { "https://picsum.photos/id/${(100..999).random()}/500/500" }

            // Generate between 0-8 comments
            val commentCount = (0..8).random()
            val comments = generateMockComments(commentCount, postId)

            // Generate a random timestamp within the last 7 days
            val randomTimeOffset = TimeUnit.DAYS.toMillis((0..7).random().toLong())
            val timestamp = currentTime - randomTimeOffset
            val userProfile = "https://picsum.photos/id/${(100..999).random()}/500/500"
            Post(
                id = postId,
                userId = userId,
                userName = userName,
                text = postText,
                imageUrls = imageUrls,
                likes = (0..150).random(),
                likedByUser = false,
                comments = comments.toMutableList(),
                timestamp = timestamp,
                userProfile = userProfile
            )
        }
    }

    /**
     * Generates a list of mock comments
     *
     * @param count The number of comments to generate
     * @param postId The post ID these comments belong to
     * @return List of Comment objects with random data
     */
    private fun generateMockComments(count: Int, postId: String): List<Comment> {
        val currentTime = System.currentTimeMillis()

        return List(count) {
            val commentId = "comment_${UUID.randomUUID()}"
            val userId = "user_${UUID.randomUUID().toString().substring(0, 6)}"
            val userName = userNames.random()
            val commentText = commentTexts.random()

            // Generate a random timestamp after the post but before now
            val randomTimeOffset = TimeUnit.HOURS.toMillis((0..24).random().toLong())
            val timestamp = currentTime - randomTimeOffset

            Comment(
                id = commentId,
                userId = userId,
                userName = userName,
                text = commentText,
                timestamp = timestamp
            )
        }
    }

    /**
     * Formats a timestamp into a human-readable string
     *
     * @param timestamp The timestamp in milliseconds
     * @return A string like "2h ago", "3d ago", etc.
     */
    fun getTimeAgo(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diffTime = currentTime - timestamp

        return when {
            diffTime < TimeUnit.MINUTES.toMillis(1) -> "just now"
            diffTime < TimeUnit.HOURS.toMillis(1) -> "${diffTime / TimeUnit.MINUTES.toMillis(1)}m ago"
            diffTime < TimeUnit.DAYS.toMillis(1) -> "${diffTime / TimeUnit.HOURS.toMillis(1)}h ago"
            diffTime < TimeUnit.DAYS.toMillis(7) -> "${diffTime / TimeUnit.DAYS.toMillis(1)}d ago"
            else -> "${diffTime / TimeUnit.DAYS.toMillis(7)}w ago"
        }
    }
}