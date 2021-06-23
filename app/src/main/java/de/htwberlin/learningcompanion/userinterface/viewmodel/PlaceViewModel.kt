package de.htwberlin.learningcompanion.userinterface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.persistance.repository.GoalRepository
import de.htwberlin.learningcompanion.persistance.repository.PlaceRepository

/**
 * Created by Max on 2019-06-02.
 *
 * @author Max Oehme
 */
class PlaceViewModel(
        val repository: PlaceRepository
): ViewModel() {

    val currentPlace: MutableLiveData<Place?> by lazy {
        MutableLiveData<Place?>()
    }

    val places: LiveData<List<Place>> by lazy {
        repository.getAllPlacesLiveData()
    }

    fun deletePlace() {
        currentPlace.value?.let {
            repository.deletePlace(it)
        }
        currentPlace.value = null
    }

}