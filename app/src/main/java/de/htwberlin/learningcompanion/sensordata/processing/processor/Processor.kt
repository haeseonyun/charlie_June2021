package de.htwberlin.learningcompanion.sensordata.processing.processor

import de.htwberlin.learningcompanion.sensordata.SensorData
import de.htwberlin.learningcompanion.sensordata.SensorValues

/**
 * Created by Max on 2019-07-02.
 *
 * @author Max Oehme
 */
interface Processor {

    fun useSensorValues() : Array<SensorData>

    fun getName() : String

    fun process(rawValueMap: HashMap<SensorValues, Float>) : String

}