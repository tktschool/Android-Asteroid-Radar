package com.udacity.asteroidradar.api

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.MainApplication
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.nasa.gov/neo/rest/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val client = OkHttpClient.Builder()
    .addInterceptor(
        ChuckerInterceptor.Builder(MainApplication.instance.applicationContext)
            .collector(ChuckerCollector(MainApplication.instance.applicationContext))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    )
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()



interface AsteroidsApiService {

    @GET("feed")
    suspend fun getFeeds(@Query("start_date") startDate: String, @Query("api_key") apiKey: String): String

}

object  AsteroidsApi {
    val retrofitService : AsteroidsApiService by lazy { retrofit.create(AsteroidsApiService::class.java) }
}