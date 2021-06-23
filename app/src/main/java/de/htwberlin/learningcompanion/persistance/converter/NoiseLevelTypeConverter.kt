package de.htwberlin.learningcompanion.persistance.converter

import androidx.room.TypeConverter
import de.htwberlin.learningcompanion.userinterface.fragment.learning.NoiseLevel

class NoiseLevelTypeConverter {

    @TypeConverter
    fun toNoiseLevel(value: Int?): NoiseLevel? {
        return if (value == null) null else NoiseLevel.values()[value]
    }

    @TypeConverter
    fun toInt(value: NoiseLevel?): Int? {
        return value?.ordinal
    }
}