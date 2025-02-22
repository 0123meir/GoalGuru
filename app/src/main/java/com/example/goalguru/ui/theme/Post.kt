package com.example.goalguru.ui.theme

data class Post(
    val userName: String,
    val text: String,
    val images: List<String>,
    var likes: Int
)
