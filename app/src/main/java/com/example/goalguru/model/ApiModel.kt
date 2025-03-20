package com.example.goalguru.model

import androidx.room.Query
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface QuotableApiService {
    @GET("random")
    fun getRandomQuote(@Query("tags") tags: String): Call<QuoteResponse>
}

data class QuoteResponse(
    val content: String,
    val author: String
)

object RetrofitClient {
    private const val BASE_URL = "https://api.quotable.io/"

    val instance: QuotableApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(QuotableApiService::class.java)
    }
}