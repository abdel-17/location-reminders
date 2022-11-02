package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SaveReminderViewModel(
    app: Application,
    private val dataSource: ReminderDataSource
) : BaseViewModel(app) {
    
    val reminderTitle = MutableLiveData<String?>()
    
    val reminderDescription = MutableLiveData<String?>()
    
    val reminderSelectedLocationStr = MutableLiveData<String?>()
    
    val selectedPOI = MutableLiveData<PointOfInterest?>()
    
    val latitude = MutableLiveData<Double?>()
    
    val longitude = MutableLiveData<Double?>()
    
    private val _doneSaving = SingleLiveEvent<Boolean>()
    
    val doneSaving: LiveData<Boolean>
        get() = _doneSaving
    
    // Hide the save button while the reminder is saving
    val showSaveButton = _showLoading.map { isLoading -> !isLoading }
    
    /**
     * The reminder data item created from the current values.
     */
    val reminderDataItem: ReminderDataItem
        get() = ReminderDataItem(
            reminderTitle.value,
            reminderDescription.value,
            reminderSelectedLocationStr.value,
            latitude.value,
            longitude.value
        )
    
    /**
     * Validate the entered data, then if it's valid saves it to the DataSource.
     */
    fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        // Check if the user entered the title and location.
        if (reminderData.title.isNullOrEmpty()) {
            _showSnackbar.value = app.getString(R.string.err_enter_title)
        } else if (reminderData.location.isNullOrEmpty()) {
            _showSnackbar.value = app.getString(R.string.err_select_location)
        } else {
            // Data is valid.
            saveReminder(reminderData)
        }
    }
    
    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        viewModelScope.launch {
            _showLoading.value = true
            dataSource.saveReminder(reminderData.toReminderDTO())
            _showLoading.value = false
            _doneSaving.value = true
        }
    }
    
    /**
     * Clears the live data objects to start fresh
     * the next time the view model gets called.
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        selectedPOI.value = null
        latitude.value = null
        longitude.value = null
    }
}