package com.example.goalguru.model

data class Goal(
    var name: String,
    var deadline: String,
    var tasks: MutableList<Task>
)
