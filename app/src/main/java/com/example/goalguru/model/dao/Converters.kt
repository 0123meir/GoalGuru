package com.example.goalguru.model.dao

import androidx.room.TypeConverter
import com.example.goalguru.model.Comment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromCommentString(value: String): List<Comment> {
        val listType = object : TypeToken<List<Comment>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCommentList(list: List<Comment>): String {
        return Gson().toJson(list)
    }
}