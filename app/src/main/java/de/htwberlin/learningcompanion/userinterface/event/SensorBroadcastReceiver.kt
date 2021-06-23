package de.htwberlin.learningcompanion.userinterface.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.htwberlin.learningcompanion.COMM_SENSORDATA_PROCESSED
import de.htwberlin.learningcompanion.COMM_SENSORDATA_RAW
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.sensordata.processing.SensorProcessingService
import de.htwberlin.learningcompanion.sensordata.source.SensorService

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
class SensorBroadcastReceiver: BroadcastReceiver() {

    var onRawSensorValueUpdateListener: ((HashMap<SensorValues, Float>) -> Unit)? = null
    var onProcessedSensorUpdateListener: ((HashMap<String, String>) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                COMM_SENSORDATA_RAW -> {
                    val valueMap = HashMap<SensorValues, Float>()
                    mapOf<SensorValues, Float>().apply {
                        SensorService.definedRawSensor.forEach {
                            it.sensorValues.forEach { sensorValues ->
                                valueMap[sensorValues] = intent.getFloatExtra(sensorValues.name, 0F)
                            }
                        }
                    }
                    onRawSensorValueUpdateListener?.invoke(valueMap)

                }
                COMM_SENSORDATA_PROCESSED -> {
                    val valueMap = HashMap<String, String>()
                    mapOf<String, String>().apply {
                        SensorProcessingService.processors.forEach {
                            valueMap[it.getName()] = intent.getStringExtra(it.getName()) ?: "EMPTY"
                        }
                    }

                    onProcessedSensorUpdateListener?.invoke(valueMap)
                }
            }
        }
    }
}