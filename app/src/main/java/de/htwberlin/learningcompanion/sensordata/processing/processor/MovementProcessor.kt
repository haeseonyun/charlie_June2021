package de.htwberlin.learningcompanion.sensordata.processing.processor

import de.htwberlin.learningcompanion.sensordata.SensorData
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.sensordata.processing.ProcessorNames

/**
 * Created by Max on 2019-07-02.
 *
 * @author Max Oehme
 */
class MovementProcessor : Processor {
    enum class State {
        MOVED, IDLE
    }

    override fun useSensorValues() = arrayOf( SensorData.ACCELEROMETER, SensorData.LINEAR_ACCELERATION )

    override fun getName() = ProcessorNames.MOVEMENT.name

    override fun process(rawValueMap: HashMap<SensorValues, Float>) : String {

        val threshold = 0.5
        val isMoved = rawValueMap[SensorValues.LINEAR_ACCELERATION_X]!! !in -threshold..threshold ||
                rawValueMap[SensorValues.LINEAR_ACCELERATION_Y]!! !in -threshold..threshold ||
                rawValueMap[SensorValues.LINEAR_ACCELERATION_Z]!! !in -threshold..threshold

        val state = if (isMoved) {
            State.MOVED
        } else {
            State.IDLE
        }

        return state.name
    }
}