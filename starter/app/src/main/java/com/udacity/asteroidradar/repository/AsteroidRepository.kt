package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.domain.asDomainModel
import com.udacity.asteroidradar.main.ApiStatus
import com.udacity.asteroidradar.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {

    private val _pictureOfDayApiStatus = MutableLiveData<ApiStatus>()
    val pictureOfDayApiStatus: LiveData<ApiStatus>
        get() = _pictureOfDayApiStatus

    val asteroidsToday: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsToday(getToDaysFormattedDates())) {
            it.asDomainModel()
        }

    val asteroidsWeek: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsWeek(
                getToDaysFormattedDates(),
                getEndWeekFormattedDates()
            )
        ) {
            it.asDomainModel()
        }

    val asteroidsSave: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidSave(getToDaysFormattedDates())) {
            it.asDomainModel()
        }

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val result = AsteroidsApi.retrofitService.getFeeds(
                    getToDaysFormattedDates(),
                    Constants.API_KEY
                )
                val asteroidsList =
                    parseAsteroidsJsonResult(JSONObject(result)).toList().asDomainModel()

                database.asteroidDao.insertAll(*NetworkAsteroidContainer(asteroidsList).asDatabaseModel())
            } catch (e: Exception) {
                e.message?.let { Log.e("MainViewModel", e.message!!) }
            }
        }
    }

    suspend fun getImageOfDay() {
        withContext(Dispatchers.Main) {
            try {
                _pictureOfDayApiStatus.value = ApiStatus.LOADING
                val result =
                    AsteroidsApi.retrofitService.getImageOfDay(Constants.API_KEY)
                _pictureOfDay.value = result
                _pictureOfDayApiStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                _pictureOfDayApiStatus.value = ApiStatus.ERROR
                _pictureOfDay.value = null
                e.message?.let { Log.e("MainViewModel", e.message!!) }
            }
        }
    }

}