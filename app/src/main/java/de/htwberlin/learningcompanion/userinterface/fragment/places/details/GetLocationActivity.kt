package de.htwberlin.learningcompanion.userinterface.fragment.places.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.htwberlin.learningcompanion.model.Address
import de.htwberlin.learningcompanion.net.geocoding.LocationToAddressRequest
import kotlinx.android.synthetic.main.activity_get_location.*
import kotlinx.android.synthetic.main.content_get_location.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


val LOCATION_DISPLAYNAME_EXTRA = "location_name_extra"
val LOCATION_LATITUDE_EXTRA = "location_latitude_extra"
val LOCATION_LONGITUDE_EXTRA = "location_longitude_extra"

/**
 * An activity that displays a map showing the place at the device's current location.
 */
class GetLocationActivity : AppCompatActivity() {
    private var map: MapView? = null

    private val defaultLocation = GeoPoint(52.504043, 13.393236)
    private var locationPermissionGranted: Boolean = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null

    private var address: Address? = null

    private lateinit var locationClickMarker: Marker


    private lateinit var gpsLocationProvider: IMyLocationProvider

    companion object {
        private val TAG = GetLocationActivity::class.java.simpleName
        private val DEFAULT_WIDE_ZOOM = 12.0
        private val DEFAULT_DETAILED_ZOOM = 15.0
        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    private lateinit var mLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(de.htwberlin.learningcompanion.R.layout.activity_get_location)

        setSupportActionBar(toolbar)
        toolbarTitle.text = "Get location of current place"

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(ctx))

        if (!locationPermissionGranted) {
            getLocationPermission()
        }

        map = findViewById(de.htwberlin.learningcompanion.R.id.map)
        map?.setTileSource(TileSourceFactory.MAPNIK)

        map?.setBuiltInZoomControls(true)
        map?.setMultiTouchControls(true)

        map?.controller?.setZoom(DEFAULT_WIDE_ZOOM)
        moveCameraToLocation(defaultLocation)

        locationClickMarker = Marker(map)
        locationClickMarker.title = "Your selected position"

        addLocationOverlay()
        addLocationClickListener()

        btn_save_location.setOnClickListener {
            onSaveButtonClick()
        }

        if (!isConnectedToNetwork(applicationContext)) {
            showNoNetworkDialog()
        }

        // if GPS is not allowed
        if (!locationPermissionGranted) {
            toast("please tap where you are in the map")
        }
    }

    private fun showNoNetworkDialog() {
        alert("You need to be connected to the internet to use the map.", "No network connection") {
            positiveButton("Leave") { finish() }
        }.show()
    }

    private fun getAddress(geoPoint: GeoPoint) {
        val request = LocationToAddressRequest(applicationContext)

        request.getAddress(geoPoint, object : LocationToAddressRequest.Callback {
            override fun onResult(addressResult: Address) {
                tv_address.text = addressResult.display_name
                address = addressResult
                Log.d(TAG, "address: $addressResult")
            }

            override fun onError(errorMessage: String) {
                Log.e(TAG, errorMessage)
            }
        })

    }

    private fun addLocationClickListener() {
        var mReceive = object : MapEventsReceiver {
            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }

            override fun singleTapConfirmedHelper(geoPoint: GeoPoint?): Boolean {
                if (geoPoint != null) {
                    getAddress(geoPoint)
                    placeMarker(geoPoint)
                }
                return true
            }

        }

        var mapEventsOverlay = MapEventsOverlay(getBaseContext(), mReceive)
        map?.overlays?.add(mapEventsOverlay)
    }

    private fun placeMarker(geoPoint: GeoPoint) {
        locationClickMarker.position = geoPoint
        locationClickMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(locationClickMarker)
    }

    private fun onSaveButtonClick() {
        if (address == null) {
            toast("Please get the address first")
        } else {
            val intent = Intent()
            intent.putExtra(LOCATION_DISPLAYNAME_EXTRA, address!!.display_name)
            intent.putExtra(LOCATION_LATITUDE_EXTRA, address!!.lat)
            intent.putExtra(LOCATION_LONGITUDE_EXTRA, address!!.lon)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    public override fun onResume() {
        super.onResume()
        map?.onResume()
        startLocationProvider()

    }

    public override fun onPause() {
        super.onPause()
        map?.onPause()
        stopLocationProvider()
    }

    private fun addLocationOverlay() {
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map)
        mLocationOverlay.enableMyLocation()
        map?.overlays?.add(mLocationOverlay)
    }

    private fun startLocationProvider() {
        if (locationPermissionGranted) {
            gpsLocationProvider = GpsMyLocationProvider(applicationContext)
            val location = gpsLocationProvider.lastKnownLocation
            Log.d(TAG, "last known location $location")

            gpsLocationProvider.startLocationProvider { location, source ->
                // only move camera at first location sight
                if (lastKnownLocation == null && location != null) {
                    moveCameraToLocation(location)
                    getAddress(GeoPoint(location.latitude, location.longitude))
                }

                lastKnownLocation = location
                Log.d(TAG, "location changed to ${location?.latitude} ${location?.longitude}")
            }
        }
    }

    private fun stopLocationProvider() {
        if (::gpsLocationProvider.isInitialized)
            gpsLocationProvider.stopLocationProvider()
    }

    private fun moveCameraToLocation(location: Location?) {
        if (location != null) {
            val geoPoint = GeoPoint(location)
            map?.controller?.setCenter(geoPoint)
            map?.controller?.setZoom(DEFAULT_DETAILED_ZOOM)
        }
    }

    private fun moveCameraToLocation(geoPoint: GeoPoint) {
        map?.controller?.setCenter(geoPoint)
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

            }
        } else {
            locationPermissionGranted = true
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    startLocationProvider()
                }
            }
        }
    }

    fun isConnectedToNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        var isConnected = false
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetworkInfo
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        return isConnected
    }
}