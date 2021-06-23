package de.htwberlin.learningcompanion.sensordata.processing.processor

import de.htwberlin.learningcompanion.sensordata.SensorData
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.sensordata.processing.ProcessorNames

/**
 * Created by Max on 2019-07-02.
 *
 * @author Max Oehme
 */
class OrientationProcessor : Processor {
    enum class State {
        IDLE, PICK_UP, UPSIDE_DOWN, LANDSCAPE
    }

    override fun useSensorValues() = arrayOf( SensorData.ACCELEROMETER, SensorData.LINEAR_ACCELERATION )

    override fun getName() = ProcessorNames.ORENTATION.name

    override fun process(rawValueMap: HashMap<SensorValues, Float>) : String {

        val idleThreshold = 9
        val pickupThreshold = 4
        val updownThreshold = -9

        val state = when {
            rawValueMap[SensorValues.ACCELEROMETER_Z]!! > idleThreshold -> State.IDLE
            rawValueMap[SensorValues.ACCELEROMETER_Y]!! > pickupThreshold -> State.PICK_UP
            rawValueMap[SensorValues.ACCELEROMETER_Z]!! < updownThreshold -> State.UPSIDE_DOWN
            rawValueMap[SensorValues.ACCELEROMETER_X]!! !in -5f..5f && rawValueMap[SensorValues.ACCELEROMETER_Z]!! > 3 -> State.LANDSCAPE
            else -> State.IDLE
        }

        return state.name
    }
}