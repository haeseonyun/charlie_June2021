package de.htwberlin.learningcompanion.network

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import de.htwberlin.learningcompanion.net.geocoding.LocationToAddressRequest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint
import java.util.concurrent.Semaphore

class LocationToAddressRequestTest {

    private val TAG = LocationToAddressRequestTest::class.java.simpleName

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun getAddressForLocation() {
        val semaphore = Semaphore(0)

        val location = GeoPoint(20.0, 20.0)
        var result: String? = null

        val request = LocationToAddressRequest(context)
        request.getAddress(location, object : LocationToAddressRequest.Callback {
            override fun onResult(address: String) {
                result = address

                Log.d(TAG, "address: $address")
                semaphore.release()
            }

            override fun onError(errorMessage: String) {
                Log.d(TAG, errorMessage)
                semaphore.release()
            }
        })

        semaphore.acquire()

        assertNotNull(result)
    }
}