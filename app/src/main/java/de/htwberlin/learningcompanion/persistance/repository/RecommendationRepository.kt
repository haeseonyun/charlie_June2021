package de.htwberlin.learningcompanion.persistance.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.htwberlin.learningcompanion.persistance.AppDatabase
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.Place

class RecommendationRepository(
        val context: Context
) {

    private val appDatabase: AppDatabase = AppDatabase.get(this.context)

    fun getGoalsByDescendingUserRating(): List<Goal> = appDatabase.recoomendDao().getGoalsByDescendingUserRating()

    fun getPlacesByDescendingUserRating(): List<Place> = appDatabase.recoomendDao().getPlacesByDescendingUserRating()

    fun getBestDuration(): Long = appDatabase.recoomendDao().getBestDuration()
}
