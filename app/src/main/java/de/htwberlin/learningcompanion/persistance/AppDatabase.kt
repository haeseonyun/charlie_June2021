package de.htwberlin.learningcompanion.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.htwberlin.learningcompanion.persistance.converter.*
import de.htwberlin.learningcompanion.persistance.dao.GoalDAO
import de.htwberlin.learningcompanion.persistance.dao.LearningSessionDAO
import de.htwberlin.learningcompanion.persistance.dao.PlaceDAO
import de.htwberlin.learningcompanion.persistance.dao.RecommendationDAO
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.model.Place

@Database(
        entities = [Goal::class, Place::class, LearningSession::class],
        version = 1)
@TypeConverters(
        UriTypeConverter::class, DateTypeConverter::class,
        LightLevelTypeConverter::class, NoiseLevelTypeConverter::class, ListConverter::class, HashMapConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDAO

    abstract fun placeDao(): PlaceDAO

    abstract fun sessionDao(): LearningSessionDAO

    abstract fun recoomendDao(): RecommendationDAO


    companion object {
        fun get(context: Context) = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "database_new.db").allowMainThreadQueries().build()
    }
}