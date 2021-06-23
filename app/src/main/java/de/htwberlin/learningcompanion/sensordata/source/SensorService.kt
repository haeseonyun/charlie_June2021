package de.htwberlin.learningcompanion.sensordata.source

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import de.htwberlin.learningcompanion.COMM_SENSORDATA_RAW
import de.htwberlin.learningcompanion.sensordata.SensorData
import de.htwberlin.learningcompanion.sensordata.SensorValues
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
class SensorService : Service(), SensorEventListener {

    companion object {
        const val sampleRate = 500L

        val definedRawSensor = arrayOf(
                SensorData.ACCELEROMETER,
                SensorData.LINEAR_ACCELERATION,
                SensorData.GYROSCOPE,
                SensorData.LIGHT
        )
    }

    val binder = LocalBinder()
    val running = AtomicBoolean(false)

    private lateinit var sensorManager: SensorManager
    private val sensorMap = ArrayList<Sensor>()
    private val sensorData = HashMap<SensorValues, Float>()

    fun enable() {
        if (running.compareAndSet(false, true)) {
            sensorMap.forEach {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            handler.post(dataDispatcher)
        }
    }

    fun disable() {
        if (running.compareAndSet(true, false)) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->

            definedRawSensor.forEach { sensor ->
                if (sensor.type == sensorEvent.sensor.type) {

                    sensor.sensorValues.forEach {
                        sensorData[it] = sensorEvent.values[it.id]
                    }

                }
            }
        }
    }

    private val handler = Handler()
    private val dataDispatcher = object : Runnable {
        override fun run() {

            val intent = Intent(COMM_SENSORDATA_RAW).apply {
                sensorData.entries.forEach {
                    putExtra(it.key.name, it.value)
                }
            }
            sendBroadcast(intent)

            if (running.get()) {
                handler.postDelayed(this, sampleRate)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        definedRawSensor.forEach {
            sensorMap.add(sensorManager.getDefaultSensor(it.type))
        }
    }

    inner class LocalBinder : Binder() {
        val service = this@SensorService
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onUnbind(intent: Intent?): Boolean {
        disable()
        return super.onUnbind(intent)
    }
}