package de.htwberlin.learningcompanion.persistance.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "place")
data class Place(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        val imageUri: Uri?,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val addressString: String?,
        var createdAtTimestamp: Long
)