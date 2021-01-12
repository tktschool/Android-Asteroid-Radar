package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.network.AsteroidsApi
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class ApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application)  {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

//    private val _asteroid = MutableLiveData<List<Asteroid>>()
//    val asteroid: LiveData<List<Asteroid>>
//        get() = _asteroid

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigationToDetail = MutableLiveData<Asteroid>()
    val navigationToDetail: LiveData<Asteroid>
        get() = _navigationToDetail

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val asteroid = asteroidRepository.videos

    init {

        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
        getImageOfDay()
    }

//    private fun getAsteroids() {
//        viewModelScope.launch {
//            _status.value = ApiStatus.LOADING
//            try {
//                val result =
//                    AsteroidsApi.retrofitService.getFeeds("2021-01-11", API_KEY)
//                val json = parseAsteroidsJsonResult(JSONObject(result))
//                _asteroid.value = json
//                _status.value = ApiStatus.DONE
//            } catch (e: Exception) {
//                _asteroid.value = arrayListOf()
//                _status.value = ApiStatus.ERROR
//                e.message?.let { Log.e("MainViewModel", e.message!!) }
//            }
//        }
//    }

    private fun getImageOfDay() {
        viewModelScope.launch {
            try {
                val result =
                    AsteroidsApi.retrofitService.getImageOfDay(API_KEY)
                _pictureOfDay.value = result
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _pictureOfDay.value = null
                _status.value = ApiStatus.ERROR
                e.message?.let { Log.e("MainViewModel", e.message!!) }
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigationToDetail.value = asteroid
    }

    fun onAsteroidNavigated(){
        _navigationToDetail.value = null
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}