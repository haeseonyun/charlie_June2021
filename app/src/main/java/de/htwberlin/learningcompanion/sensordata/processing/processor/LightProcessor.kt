package de.htwberlin.learningcompanion.sensordata.processing.processor

import de.htwberlin.learningcompanion.sensordata.SensorData
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.sensordata.processing.ProcessorNames

/**
 * Created by Max on 2019-07-02.
 *
 * @author Max Oehme
 */
class LightProcessor : Processor {

    override fun useSensorValues() = arrayOf( SensorData.LIGHT )

    override fun getName() = ProcessorNames.LIGHT.name

    override fun process(rawValueMap: HashMap<SensorValues, Float>) : String {
        return rawValueMap[SensorValues.LIGHT].toString()
    }
}