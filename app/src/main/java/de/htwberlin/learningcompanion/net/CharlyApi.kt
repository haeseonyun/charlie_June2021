package de.htwberlin.learningcompanion.net

import de.htwberlin.learningcompanion.net.model.ApiGoal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CharlyApi {

    @POST("/user/goal/add")
    suspend fun addGoal(@Body goal: ApiGoal): Response<String>
}