package com.example.goalguru.model

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotableApiService {
    @GET("random")
    fun getRandomQuote(): Call<List<QuoteResponse>>
}

// Object as returned from API
data class QuoteResponse(
    @SerializedName("q") val content: String,
    @SerializedName("a") val author: String
    // 'h' element is being ignored
)

object RetrofitClient {
    private const val BASE_URL = "https://zenquotes.io/api/"

    val instance: QuotableApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(QuotableApiService::class.java)
    }
}