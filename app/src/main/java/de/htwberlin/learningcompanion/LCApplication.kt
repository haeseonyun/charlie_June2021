package de.htwberlin.learningcompanion

import android.app.Application
import android.content.*
import android.os.IBinder
import de.htwberlin.learningcompanion.sensordata.processing.SensorProcessingService
import de.htwberlin.learningcompanion.sensordata.source.SensorService
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
class LCApplication: Application() {

    companion object {
        var app: LCApplication? = null
    }


    private val sensorService = object : ServiceConnection {
        var service: SensorService? = null

        override fun onServiceDisconnected(name: ComponentName?) {
            service?.disable()
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder != null) {
                (binder as SensorService.LocalBinder).service.let {
                    service = it
                    it.enable()
                }
            }
        }
    }

    private val sensorProcessingService = object : ServiceConnection {
        var service: SensorProcessingService? = null

        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder != null) {
                (binder as SensorProcessingService.LocalBinder).service.let {
                    service = it
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this


        bindService(Intent(this, SensorService::class.java), sensorService, Context.BIND_AUTO_CREATE)
        bindService(Intent(this, SensorProcessingService::class.java), sensorProcessingService, Context.BIND_AUTO_CREATE)

        registerReceiver(ViewModelFactory.repositorySensor, IntentFilter().apply {
            addAction(COMM_SENSORDATA_RAW)
            addAction(COMM_SENSORDATA_PROCESSED)
        })
    }
}