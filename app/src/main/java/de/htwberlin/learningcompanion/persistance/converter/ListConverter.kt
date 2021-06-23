package de.htwberlin.learningcompanion.persistance.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ListConverter {

    @TypeConverter
    fun fromIntString(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {

        }.type
        return Gson().fromJson<Any>(value, listType) as List<Int>
    }

    @TypeConverter
    fun fromIntArrayList(list: List<Int>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromFloatString(value: String): List<Float> {
        val listType = object : TypeToken<List<Float>>() {

        }.type
        return Gson().fromJson<Any>(value, listType) as List<Float>
    }

    @TypeConverter
    fun fromFloatArrayList(list: List<Float>): String {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun fromLongString(value: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {

        }.type
        return Gson().fromJson<Any>(value, listType) as List<Long>
    }

    @TypeConverter
    fun fromLongArrayList(list: List<Long>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}