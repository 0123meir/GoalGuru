package com.example.goalguru.models

data class Goal(
    var name: String,
    var deadline: String,
    var tasks: MutableList<Task>
)
