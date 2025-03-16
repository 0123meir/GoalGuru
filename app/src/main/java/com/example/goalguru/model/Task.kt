package com.example.goalguru.model

data class Task(
    var goal: String,
    var description: String,
    var deadline: Int,
    var isChecked: Boolean
)
