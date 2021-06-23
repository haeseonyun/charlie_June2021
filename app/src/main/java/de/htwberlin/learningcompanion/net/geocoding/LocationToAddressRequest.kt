package de.htwberlin.learningcompanion.net.geocoding

import android.content.Context
import de.htwberlin.learningcompanion.model.Address
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LocationToAddressRequest(val context: Context) : Callback<Address> {

    interface Callback {
        fun onResult(address: Address)
        fun onError(errorMessage: String)
    }

    private lateinit var callback: Callback

    private val BASE_URL = "https://nominatim.openstreetmap.org/"

    fun getAddress(location: GeoPoint, callback: Callback) {
        this.callback = callback

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val nominatimAPI = retrofit.create<NominatimAPI>(NominatimAPI::class.java)

        val call = nominatimAPI.getAddress("json", location.latitude, location.longitude)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<Address>, response: Response<Address>) {
        if (response.isSuccessful) {
            val address = response.body()
            System.out.println(address?.display_name)
            callback.onResult(address!!)
        } else {
            System.out.println(response.errorBody())
        }
    }

    override fun onFailure(call: Call<Address>, t: Throwable) {
        t.printStackTrace()
        callback.onError(t.message!!)
    }
}