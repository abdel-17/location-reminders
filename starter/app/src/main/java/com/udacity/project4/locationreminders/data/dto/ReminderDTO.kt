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
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "latitude") var latitude: Double?,
    @ColumnInfo(name = "longitude") var longitude: Double?,
    @PrimaryKey @ColumnInfo(name = "entry_id") val id: String = UUID.randomUUID().toString()
) {
    fun toReminderDataItem() = ReminderDataItem(
        title, description, location, latitude, longitude, id
    )
}
