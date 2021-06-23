package de.htwberlin.learningcompanion.persistance.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.HashMap

class HashMapConverter {

    @TypeConverter
    fun fromIntString(value: String): HashMap<Long, String> {
        val type = object : TypeToken<HashMap<Long, String>>() {

        }.type
        return Gson().fromJson<Any>(value, type) as HashMap<Long, String>
    }

    @TypeConverter
    fun fromIntArrayList(map: HashMap<Long, String>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}