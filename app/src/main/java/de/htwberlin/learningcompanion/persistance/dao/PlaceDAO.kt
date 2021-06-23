package de.htwberlin.learningcompanion.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.htwberlin.learningcompanion.persistance.model.Place

@Dao
interface PlaceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: Place)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaces(places: List<Place>)

    @Update
    fun updatePlace(place: Place)

    @Update
    fun updatePlaces(places: List<Place>)

    @Delete
    fun deletePlace(place: Place)

    @Query("SELECT * FROM place WHERE id == :id")
    fun getPlaceByID(id: Long): Place

    @Query("SELECT * FROM place")
    fun getPlaces(): List<Place>

    @Query("SELECT * FROM place")
    fun getPlacesAsLiveData(): LiveData<List<Place>>

    @Query("SELECT * FROM place ORDER BY id DESC LIMIT 1")
    fun getNewestPlace(): Place
}