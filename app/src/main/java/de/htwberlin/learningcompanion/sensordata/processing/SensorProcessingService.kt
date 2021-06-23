package de.htwberlin.learningcompanion.sensordata.processing

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import de.htwberlin.learningcompanion.COMM_SENSORDATA_PROCESSED
import de.htwberlin.learningcompanion.COMM_SENSORDATA_RAW
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.sensordata.processing.processor.LightProcessor
import de.htwberlin.learningcompanion.sensordata.processing.processor.MovementProcessor
import de.htwberlin.learningcompanion.sensordata.processing.processor.OrientationProcessor
import de.htwberlin.learningcompanion.sensordata.processing.processor.Processor
import de.htwberlin.learningcompanion.sensordata.source.SensorService

/**
 * Created by Max on 2019-06-26.
 *
 * @author Max Oehme
 */
class SensorProcessingService : Service() {

    companion object {
        val processors: Array<Processor> = arrayOf(
                OrientationProcessor(),
                MovementProcessor(),
                LightProcessor()
        )
    }

    private fun processSensorData(valueMap: HashMap<SensorValues, Float>) {

        val processedMap = HashMap<String, String>()
        processors.forEach {  processor ->
            val value = processor.process(valueMap)

            processedMap[processor.getName()] = value

        }

        val intent = Intent(COMM_SENSORDATA_PROCESSED).apply {
            processedMap.entries.forEach {
                putExtra(it.key, it.value)
            }
        }
        sendBroadcast(intent)
    }

    private val sensorReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (intent.action == COMM_SENSORDATA_RAW) {
                    val valueMap = HashMap<SensorValues, Float>()
                    mapOf<SensorValues, Float>().apply {
                        SensorService.definedRawSensor.forEach {
                            it.sensorValues.forEach { sensorValues ->
                                valueMap[sensorValues] = intent.getFloatExtra(sensorValues.name, 0F)
                            }
                        }
                    }

                    processSensorData(valueMap)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(sensorReceiver, IntentFilter().apply {
            addAction(COMM_SENSORDATA_RAW)
        })
    }

    val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        val service = this@SensorProcessingService
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onUnbind(intent: Intent?): Boolean {
        unregisterReceiver(sensorReceiver)
        return super.onUnbind(intent)
    }
}