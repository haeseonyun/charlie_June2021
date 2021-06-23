package de.htwberlin.learningcompanion.userinterface.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.userinterface.event.SensorBroadcastReceiver

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
class SensorViewModel(repository: SensorBroadcastReceiver) : ViewModel() {


    val sensorDataLiveData = object : MutableLiveData<HashMap<SensorValues, Float>>()  {
        private val listener = { update: HashMap<SensorValues, Float> ->
            value = update
        }

        override fun onInactive() {
            repository.onRawSensorValueUpdateListener = null
        }

        override fun onActive() {
            repository.onRawSensorValueUpdateListener = listener
        }
    }


}