package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.io.Serializable
import java.util.*

/**
 * A data class that acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    val title: String,
    var description: String?,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val id: String = UUID.randomUUID().toString()
) : Serializable {
    
    fun toReminderDTO() = ReminderDTO(title, description, location, latitude, longitude, id)
    
    fun describeLocation() = String.format(
        "$location at (%1.2f, %2.2f)",
        latitude, longitude
    )
}