package de.htwberlin.learningcompanion.net.geocoding

import de.htwberlin.learningcompanion.model.Address
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimAPI {

    @GET("reverse/")
    fun getAddress(@Query("format") format: String, @Query("lat") latitude: Double, @Query("lon") longitude: Double): Call<Address>
}