package de.htwberlin.learningcompanion.persistance.converter

import androidx.room.TypeConverter
import de.htwberlin.learningcompanion.userinterface.fragment.learning.LightLevel

class LightLevelTypeConverter {

    @TypeConverter
    fun toLightLevel(value: Int?): LightLevel? {
        return if (value == null) null else LightLevel.values()[value]
    }

    @TypeConverter
    fun toInt(value: LightLevel?): Int? {
        return value?.ordinal
    }
}