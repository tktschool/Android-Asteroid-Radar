package com.udacity.asteroidradar.api

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.ImageOfDay
import com.udacity.asteroidradar.MainApplication
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.nasa.gov/"

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val retrofit_scalrar = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private val retrofit_moshi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface AsteroidsApiService {

    @GET("neo/rest/v1/feed")
    suspend fun getFeeds(@Query("start_date") startDate: String, @Query("api_key") apiKey: String): String

    @GET("planetary/apod")
    suspend fun getImageOfDay(@Query("api_key") apiKey: String): ImageOfDay

}

object  AsteroidsApi {
    val retrofitScalarService : AsteroidsApiService by lazy { retrofit_scalrar.create(AsteroidsApiService::class.java) }
    val retrofitMoshiService : AsteroidsApiService by lazy { retrofit_moshi.create(AsteroidsApiService::class.java) }
}