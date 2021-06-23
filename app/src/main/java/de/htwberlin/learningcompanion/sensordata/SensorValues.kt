package de.htwberlin.learningcompanion.sensordata

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
enum class SensorValues(
        val id: Int
) {
    ACCELEROMETER_X(0), ACCELEROMETER_Y(1), ACCELEROMETER_Z(2),
    LINEAR_ACCELERATION_X(0), LINEAR_ACCELERATION_Y(1), LINEAR_ACCELERATION_Z(2),
    GYROSCOPE_X(0), GYROSCOPE_Y(1), GYROSCOPE_Z(2),
    LIGHT(0)
}