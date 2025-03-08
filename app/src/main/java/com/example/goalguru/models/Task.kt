package com.example.goalguru.models

data class Task(
    var goal: String,
    var description: String,
    var deadline: String,
    var isChecked: Boolean
)
