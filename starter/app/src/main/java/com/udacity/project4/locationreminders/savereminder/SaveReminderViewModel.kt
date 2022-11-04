package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(
    app: Application,
    private val dataSource: ReminderDataSource
) : BaseViewModel(app) {
    
    val reminderTitle = MutableLiveData<String?>()
    
    val reminderDescription = MutableLiveData<String?>()
    
    val selectedPOI = MutableLiveData<PointOfInterest?>()
    
    // Hide the save button while the reminder is saving
    val showSaveButton = _showLoading.map { isLoading -> !isLoading }
    
    /**
     * Validate the current reminder data item, then if it's valid
     * saves it to the DataSource.
     */
    fun validateAndSaveReminder(): ReminderDataItem? {
        // Check if the user entered the title and location.
        val title = reminderTitle.value
        if (title.isNullOrEmpty()) {
            _showSnackbar.value = app.getString(R.string.err_enter_title)
            return null
        }
        val poi = selectedPOI.value
        if (poi == null) {
            _showSnackbar.value = app.getString(R.string.err_select_location)
            return null
        }
        // Data is valid.
        val reminder = ReminderDataItem(
            title,
            reminderDescription.value,
            poi.name,
            poi.latLng.latitude,
            poi.latLng.longitude
        )
        saveReminder(reminder)
        return reminder
    }
    
    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminder: ReminderDataItem) {
        viewModelScope.launch {
            _showLoading.value = true
            dataSource.saveReminder(reminder.toReminderDTO())
            _showLoading.value = false
        }
    }
    
    /**
     * Clears the live data objects to start fresh
     * the next time the view model gets called.
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        selectedPOI.value = null
    }
}