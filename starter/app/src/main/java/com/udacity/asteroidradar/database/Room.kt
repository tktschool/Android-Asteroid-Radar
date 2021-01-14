package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {

    //TODO Can i use sql between
    @Query("SELECT * FROM DatabaseAsteroid WHERE closeApproachDate >= :day AND  closeApproachDate <= :dayEndWeek ORDER BY closeApproachDate asc")
    fun getAsteroidsWeek(day:String,dayEndWeek:String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from DatabaseAsteroid where closeApproachDate >=:day order by closeApproachDate asc")
    fun getAsteroidSave(day:String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from DatabaseAsteroid where closeApproachDate = :day")
    fun getAsteroidsToday(day:String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}


@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}


private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}
