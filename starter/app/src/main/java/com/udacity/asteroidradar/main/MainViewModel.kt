package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidsApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class ApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _asteroid = MutableLiveData<List<Asteroid>>()
    val asteroid: LiveData<List<Asteroid>>
        get() = _asteroid

    init {
        getAsteroids()
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                val result = AsteroidsApi.retrofitService.getFeeds("2021-01-09","INSERT_API_KEY_HERE")
                val json =  parseAsteroidsJsonResult(JSONObject(result))
                _asteroid.value = json
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _asteroid.value = arrayListOf()
                e.message?.let { Log.i("TAG", it) }
            }
        }

        }

}