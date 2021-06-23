package de.htwberlin.learningcompanion.persistance.converter

import android.net.Uri
import androidx.room.TypeConverter


class UriTypeConverter {

    @TypeConverter
    fun toUri(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }

    @TypeConverter
    fun toString(value: Uri?): String? {
        return value?.toString()
    }
}