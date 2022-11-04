package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.launch

class RemindersListViewModel(
    app: Application,
    private val dataSource: ReminderDataSource
) : BaseViewModel(app) {
    
    private val _reminders = MutableLiveData<List<ReminderDataItem>>()
    
    /**
     * A list that holds the reminder data to be displayed on the UI
     */
    val reminders: LiveData<List<ReminderDataItem>>
        get() = _reminders
    
    /**
     * The view should show an indicator that the list is empty
     * when the value of this live data is `true`.
     */
    val showEmpty: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        // If the reminders list is empty, show a loading indicator unless we're
        // showing a loading indicator. In which case, we hide it to avoid overlap.
        addSource(_reminders) { remindersList: List<ReminderDataItem> ->
            value = remindersList.isEmpty() && _showLoading.value != true
        }
        addSource(_showLoading) { loading: Boolean ->
            value = _reminders.value.isNullOrEmpty() && !loading
        }
    }
    
    /**
     * Get all the reminders from the data source and add them to [reminders]
     * to be shown on the UI, or show an error (if any).
     */
    fun loadReminders() {
        viewModelScope.launch {
            _showLoading.value = true
            // Interacting with the data source has to be through a coroutine
            val result = dataSource.getReminders()
            _showLoading.value = false
            when (result) {
                is Result.Success -> {
                    val remindersList = result.data.map(ReminderDTO::toReminderDataItem)
                    _reminders.value = remindersList
                }
                is Result.Error -> {
                    _showSnackbar.value = result.errorMessage
                }
            }
        }
    }
}