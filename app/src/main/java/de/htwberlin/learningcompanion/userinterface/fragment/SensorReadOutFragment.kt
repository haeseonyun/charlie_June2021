package de.htwberlin.learningcompanion.userinterface.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.userinterface.viewmodel.SensorViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_sensor_read_out.*
import org.jetbrains.anko.backgroundColor

/**
 * A simple [Fragment] subclass.
 *
 */
class SensorReadOutFragment : Fragment() {


    private val viewModelFactory = ViewModelFactory
    private lateinit var sensorViewModel: SensorViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor_read_out, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sensorViewModel = ViewModelProviders.of(this, viewModelFactory).get(SensorViewModel::class.java)
        sensorViewModel.sensorDataLiveData.observe(this, sensorObserver)

    }

    private val sensorObserver = Observer<HashMap<SensorValues, Float>> {
        accelerometerX.text = it[SensorValues.ACCELEROMETER_X].toString()
        accelerometerY.text = it[SensorValues.ACCELEROMETER_Y].toString()
        accelerometerZ.text = it[SensorValues.ACCELEROMETER_Z].toString()
        linearAccelX.text = it[SensorValues.LINEAR_ACCELERATION_X].toString()
        linearAccelY.text = it[SensorValues.LINEAR_ACCELERATION_Y].toString()
        linearAccelZ.text = it[SensorValues.LINEAR_ACCELERATION_Z].toString()
        gyroscopeX.text = it[SensorValues.GYROSCOPE_X].toString()
        gyroscopeY.text = it[SensorValues.GYROSCOPE_Y].toString()
        gyroscopeZ.text = it[SensorValues.GYROSCOPE_Z].toString()
        light.text = it[SensorValues.LIGHT].toString()

        val threshold = 0.5

        if (it[SensorValues.LINEAR_ACCELERATION_X]!! > threshold) {
            directionRight.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            directionRight.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        if (it[SensorValues.LINEAR_ACCELERATION_X]!! < -threshold ) {
            directionLeft.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else{
            directionLeft.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        if (it[SensorValues.LINEAR_ACCELERATION_Y]!! > threshold) {
            directionBackward.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            directionBackward.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        if (it[SensorValues.LINEAR_ACCELERATION_Y]!! < -threshold) {
            directionForward.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            directionForward.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        if (it[SensorValues.LINEAR_ACCELERATION_Z]!! > threshold ) {
            directionDown.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            directionDown.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        if (it[SensorValues.LINEAR_ACCELERATION_Z]!! < -threshold) {
            directionUp.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            directionUp.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        axisXSeekbar.progress = ((-it[SensorValues.ACCELEROMETER_X]!! + 10) * 5).toInt().let { value ->
            when {
                value < 0 -> 0
                value > 100 -> 100
                else -> value
            }
        }

        axisYSeekbar.progress = ((it[SensorValues.ACCELEROMETER_Y]!! + 10) * 5).toInt().let { value ->
            when {
                value < 0 -> 0
                value > 100 -> 100
                else -> value
            }
        }

        axisZSeekbar.progress = ((it[SensorValues.ACCELEROMETER_Z]!! + 10) * 5).toInt().let { value ->
            when {
                value < 0 -> 0
                value > 100 -> 100
                else -> value
            }
        }


        val isMoved = it[SensorValues.LINEAR_ACCELERATION_X]!! !in -threshold..threshold ||
                it[SensorValues.LINEAR_ACCELERATION_Y]!! !in -threshold..threshold ||
                it[SensorValues.LINEAR_ACCELERATION_Z]!! !in -threshold..threshold

        if (isMoved) {
            textViewMoved.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            textViewMoved.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        val idleThreshold = 9

        val isIdleOnTable = it[SensorValues.ACCELEROMETER_Z]!! > idleThreshold && !isMoved
        if (isIdleOnTable) {
            textViewIdle.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            textViewIdle.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

        val pickupThreshold = 4
        val isPickedUp = it[SensorValues.ACCELEROMETER_Y]!! > pickupThreshold
        if (isPickedUp) {
            textViewPickup.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            textViewPickup.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }


        val updownThreshold = -9
        val isUpsideDown = it[SensorValues.ACCELEROMETER_Z]!! < updownThreshold
        if (isUpsideDown) {
            textViewUpsideDown.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            textViewUpsideDown.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }


        val isLandscape = it[SensorValues.ACCELEROMETER_X]!! !in -5f..5f &&
                it[SensorValues.ACCELEROMETER_Z]!! > 3

        if (isLandscape) {
            textViewLandscape.backgroundColor = ContextCompat.getColor(context!!, R.color.red)
        } else {
            textViewLandscape.backgroundColor = ContextCompat.getColor(context!!, R.color.white)
        }

    }

}
