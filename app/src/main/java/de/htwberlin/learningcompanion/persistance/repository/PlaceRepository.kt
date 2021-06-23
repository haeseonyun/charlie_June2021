package de.htwberlin.learningcompanion.persistance.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.htwberlin.learningcompanion.persistance.AppDatabase
import de.htwberlin.learningcompanion.persistance.model.Place

class PlaceRepository(
        val context: Context
) {

    private val appDatabase = AppDatabase.get(context)

    fun getAllPlacesLiveData(): LiveData<List<Place>> = appDatabase.placeDao().getPlacesAsLiveData()

    fun getAllPlaces(): List<Place> {
        return appDatabase.placeDao().getPlaces()
    }

    fun getPlaceByID(placeID: Long): Place {
        return appDatabase.placeDao().getPlaceByID(placeID)
    }

    fun insertPlaceList(placeList: List<Place>) {
        appDatabase.placeDao().insertPlaces(placeList)
    }

    fun insertPlace(place: Place) {
        appDatabase.placeDao().insertPlace(place)
    }

    fun insertPlace(place: Place, callback: ((Place) -> Unit)) {
        appDatabase.placeDao().insertPlace(place)

        callback.invoke(appDatabase.placeDao().getNewestPlace())
    }

    fun updatePlace(place: Place) {
        appDatabase.placeDao().updatePlace(place)
    }

    fun updatePlace(place: Place, callback: ((Place) -> Unit)) {
        appDatabase.placeDao().updatePlace(place)

        place.id?.let {
            callback.invoke(appDatabase.placeDao().getPlaceByID(it))
        }
    }

    fun deletePlace(place: Place) {
        appDatabase.placeDao().deletePlace(place)
    }
}
