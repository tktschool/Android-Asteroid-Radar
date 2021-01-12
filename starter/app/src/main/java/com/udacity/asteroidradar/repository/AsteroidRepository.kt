package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asDomainModel
import com.udacity.asteroidradar.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

class AsteroidRepository(private val database: AsteroidDatabase) {


    val videos: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {

            try {
                val result = AsteroidsApi.retrofitService.getFeeds(getToDaysFormattedDates(), Constants.API_KEY)
                val asteroidsList =
                    parseAsteroidsJsonResult(JSONObject(result)).toList().asDomainModel()

                database.asteroidDao.insertAll(*NetworkAsteroidContainer(asteroidsList).asDatabaseModel())
            } catch (e: Exception) {
                e.message?.let { Log.e("MainViewModel", e.message!!) }
            }
        }
    }
}