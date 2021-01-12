package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asDomainModel
import com.udacity.asteroidradar.network.AsteroidsApi
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository (private val database: AsteroidDatabase) {


    val videos: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val result = AsteroidsApi.retrofitService.getFeeds("2021-01-11", Constants.API_KEY)
            val asteroidsList = parseAsteroidsJsonResult(JSONObject(result)).toList().asDomainModel()

            database.asteroidDao.insertAll(*NetworkAsteroidContainer(asteroidsList).asDatabaseModel())
        }
    }
}