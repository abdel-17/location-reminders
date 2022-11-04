package com.udacity.project4.locationreminders.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import java.util.*

/**
 * Immutable class representing a reminder entity to be stored in a Room database.
 */
@Entity(tableName = "reminders")
data class ReminderDTO(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @PrimaryKey @ColumnInfo(name = "entry_id") val id: String = UUID.randomUUID().toString()
) {
    fun toReminderDataItem() = ReminderDataItem(
        title, description, location, latitude, longitude, id
    )
}
