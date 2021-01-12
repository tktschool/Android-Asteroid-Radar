package com.udacity.asteroidradar.network

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.MainApplication
import com.udacity.asteroidradar.domain.PictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
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
    .client(client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface AsteroidsApiService {

    @GET("neo/rest/v1/feed")
    suspend fun getFeeds(
        @Query("start_date") startDate: String,
        @Query("api_key") apiKey: String
    ): String

    @GET("planetary/apod")
    suspend fun getImageOfDay(@Query("api_key") apiKey: String): PictureOfDay

}

object AsteroidsApi {
    val retrofitService: AsteroidsApiService by lazy { retrofit.create(AsteroidsApiService::class.java) }
}