package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.io.Serializable
import java.util.*

/**
 * A data class that acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString()
) : Serializable {
    
    fun toReminderDTO() = ReminderDTO(title, description, location, latitude, longitude, id)
    
    fun describeLocation(): String {
        val locationFormatted = String.format(
            "(%1.2f, %2.2f)",
            latitude, longitude
        )
        return when (location) {
            null -> locationFormatted
            else -> "$location at $locationFormatted"
        }
    }
}