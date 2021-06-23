package de.htwberlin.learningcompanion.sensordata

import android.hardware.Sensor

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
enum class SensorData(
        val type: Int,
        val sensorValues: Array<SensorValues>
){
    ACCELEROMETER(
            Sensor.TYPE_ACCELEROMETER,
            arrayOf(SensorValues.ACCELEROMETER_X, SensorValues.ACCELEROMETER_Y, SensorValues.ACCELEROMETER_Z)
    ),
    LINEAR_ACCELERATION(
            Sensor.TYPE_LINEAR_ACCELERATION,
            arrayOf(SensorValues.LINEAR_ACCELERATION_X, SensorValues.LINEAR_ACCELERATION_Y, SensorValues.LINEAR_ACCELERATION_Z)
    ),
    GYROSCOPE(
            Sensor.TYPE_GYROSCOPE,
            arrayOf(SensorValues.GYROSCOPE_X, SensorValues.GYROSCOPE_Y, SensorValues.GYROSCOPE_Z)
    ),
    LIGHT(
            Sensor.TYPE_LIGHT,
            arrayOf(SensorValues.LIGHT)
    )

}