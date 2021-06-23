package de.htwberlin.learningcompanion.net

import de.htwberlin.learningcompanion.net.model.ApiGoal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "http://141.20.37.100/companion/api"

    private fun client(): CharlyApi {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(CharlyApi::class.java)
    }

    suspend fun addGoal(goal: ApiGoal) = withContext(Dispatchers.IO) {
        val client = client()
        val response = client.addGoal(goal)
    }

}